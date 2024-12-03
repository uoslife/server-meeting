package uoslife.servermeeting.match.service

import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uoslife.servermeeting.match.dao.MatchedDao
import uoslife.servermeeting.match.dto.response.*
import uoslife.servermeeting.match.entity.Match
import uoslife.servermeeting.match.exception.MatchNotFoundException
import uoslife.servermeeting.match.exception.UnauthorizedMatchAccessException
import uoslife.servermeeting.meetingteam.dao.UserTeamDao
import uoslife.servermeeting.meetingteam.dto.request.CompletionStatus
import uoslife.servermeeting.meetingteam.dto.response.MeetingTeamInformationGetResponse
import uoslife.servermeeting.meetingteam.entity.MeetingTeam
import uoslife.servermeeting.meetingteam.entity.UserTeam
import uoslife.servermeeting.meetingteam.entity.enums.TeamType
import uoslife.servermeeting.meetingteam.entity.enums.TeamType.SINGLE
import uoslife.servermeeting.meetingteam.entity.enums.TeamType.TRIPLE
import uoslife.servermeeting.meetingteam.exception.GenderNotUpdatedException
import uoslife.servermeeting.meetingteam.exception.MeetingTeamNotFoundException
import uoslife.servermeeting.meetingteam.exception.OnlyTeamLeaderCanGetMatchException
import uoslife.servermeeting.meetingteam.service.impl.SingleMeetingService
import uoslife.servermeeting.meetingteam.service.impl.TripleMeetingService
import uoslife.servermeeting.user.entity.User
import uoslife.servermeeting.user.entity.enums.GenderType
import uoslife.servermeeting.user.exception.UserNotFoundException

@Service
@Transactional(readOnly = true)
class MatchingService(
    private val matchedDao: MatchedDao,
    private val userTeamDao: UserTeamDao,
    private val singleMeetingService: SingleMeetingService,
    private val tripleMeetingService: TripleMeetingService,
) {
    @Cacheable(value = ["meeting-participation"], key = "#userId", unless = "#result == null")
    fun getUserMeetingParticipation(userId: Long): MeetingParticipationResponse {
        val userTeams = userTeamDao.findAllByUserIdWithPaymentStatus(userId)

        return MeetingParticipationResponse(
            single = getParticipationStatus(userTeams.find { it.team.type == SINGLE }),
            triple = getParticipationStatus(userTeams.find { it.team.type == TRIPLE })
        )
    }

    @Cacheable(
        value = ["match-result"],
        key = "#userId + ':' + #teamType",
        unless = "#result == null"
    )
    fun getMatchResult(userId: Long, teamType: TeamType): MatchResultResponse {
        // 미팅 참여 여부 확인
        val participation = getUserMeetingParticipation(userId)
        val participationStatus =
            when (teamType) {
                SINGLE -> participation.single
                TRIPLE -> participation.triple
            }
        if (!participationStatus.isParticipated) {
            throw MeetingTeamNotFoundException()
        }
        // 매칭 결과 조회
        val result = matchedDao.findMatchResultByUserIdAndTeamType(userId, teamType)!!

        return MatchResultResponse(
            matchType = result.teamType,
            isMatched = result.matchId != null,
            matchId = result.matchId
        )
    }

    @Cacheable(
        value = ["partner-info"],
        key = "#userId + ':' + #teamType",
        unless = "#result == null"
    )
    fun getMatchedPartnerInformation(
        userId: Long,
        teamType: TeamType
    ): MatchedPartnerInformationResponse {
        val matchResult = getMatchResult(userId, teamType)
        if (!matchResult.isMatched || matchResult.matchId == null) {
            throw MatchNotFoundException()
        }
        val response = getPartnerInformation(userId, matchResult.matchId)
        val convertedResponse = convertPersistentBagToArrayList(response)
        return MeetingDtoConverter.toMatchedPartnerInformationResponse(convertedResponse)
    }

    @Transactional
    fun getMatchedMeetingTeamByType(userId: Long, teamType: TeamType): MatchInformationResponse {
        val userTeam =
            userTeamDao.findUserWithMeetingTeam(userId, teamType) ?: throw UserNotFoundException()
        val meetingTeam = userTeam.team ?: throw MeetingTeamNotFoundException()

        if (!userTeam.isLeader) throw OnlyTeamLeaderCanGetMatchException()

        val match = getMatchByGender(userTeam.user, meetingTeam)
        val opponentTeam = getOpponentTeamByGender(userTeam.user, match)
        val opponentUser = getOpponentLeaderUser(opponentTeam)

        return MatchInformationResponse(
            getOpponentUserInformationByTeamType(meetingTeam, opponentUser)
        )
    }

    private fun getOpponentLeaderUser(opponentTeam: MeetingTeam): User {
        val leader =
            opponentTeam.userTeams
                .stream()
                .filter { userTeam -> userTeam.isLeader }
                .findFirst()
                .orElseThrow { UserNotFoundException() }

        return leader.user
    }
    fun getMatchByGender(user: User, meetingTeam: MeetingTeam): Match {
        return when (user.gender) {
            GenderType.MALE -> matchedDao.findMatchByMaleTeamWithFemaleTeam(meetingTeam)
                    ?: throw MatchNotFoundException()
            GenderType.FEMALE -> matchedDao.findMatchByFeMaleTeamWithMaleTeam(meetingTeam)
                    ?: throw MatchNotFoundException()
            null -> throw GenderNotUpdatedException()
        }
    }

    fun getOpponentTeamByGender(user: User, match: Match): MeetingTeam {
        return when (user.gender) {
            GenderType.MALE -> match.femaleTeam
            GenderType.FEMALE -> match.maleTeam
            null -> throw GenderNotUpdatedException()
        }
    }

    fun getOpponentUserInformationByTeamType(
        meetingTeam: MeetingTeam,
        opponentUser: User
    ): MatchedMeetingTeamInformationGetResponse {
        val meetingTeamInformationGetResponse =
            when (meetingTeam.type) {
                SINGLE -> {
                    singleMeetingService.getMeetingTeamInformation(
                        opponentUser.id!!,
                        CompletionStatus.COMPLETED
                    )
                }
                TRIPLE -> {
                    tripleMeetingService.getMeetingTeamInformation(
                        opponentUser.id!!,
                        CompletionStatus.COMPLETED
                    )
                }
            }
        return meetingTeamInformationGetResponse.toMatchedMeetingTeamInformationGetResponse()
    }

    private fun getParticipationStatus(userTeam: UserTeam?): ParticipationStatus {
        if (userTeam == null) {
            return ParticipationStatus(false, null)
        }

        return ParticipationStatus(isParticipated = true, meetingTeamId = userTeam.team.id)
    }

    private fun getPartnerTeam(userGender: GenderType, match: Match): MeetingTeam {
        return when (userGender) {
            GenderType.MALE -> match.femaleTeam
            GenderType.FEMALE -> match.maleTeam
        }
    }

    private fun getPartnerInformation(
        userId: Long,
        matchId: Long
    ): MeetingTeamInformationGetResponse {
        val match = matchedDao.findById(matchId) ?: throw MatchNotFoundException()
        val userTeam =
            userTeamDao.findUserWithMeetingTeamByMatchId(userId, matchId)
                ?: throw UnauthorizedMatchAccessException()
        val partnerTeam = getPartnerTeam(userTeam.team.gender, match)

        return when (partnerTeam.type) {
            SINGLE -> getPartnerSingleTeamInfo(partnerTeam)
            TRIPLE -> getPartnerTripleTeamInfo(partnerTeam)
        }
    }

    private fun getPartnerSingleTeamInfo(partnerTeam: MeetingTeam) =
        singleMeetingService.getMeetingTeamInformation(
            partnerTeam.userTeams.first().user.id!!,
            CompletionStatus.COMPLETED
        )

    private fun getPartnerTripleTeamInfo(partnerTeam: MeetingTeam) =
        tripleMeetingService.getMeetingTeamInformation(
            partnerTeam.userTeams.first { it.isLeader }.user.id!!,
            CompletionStatus.COMPLETED
        )

    private fun convertPersistentBagToArrayList(response: MeetingTeamInformationGetResponse) =
        response.copy(
            meetingTeamUserProfiles =
                response.meetingTeamUserProfiles?.map { profile ->
                    profile.copy(interest = profile.interest?.let { ArrayList(it) })
                }
        )
}
