package uoslife.servermeeting.match.service

import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uoslife.servermeeting.match.dao.MatchedDao
import uoslife.servermeeting.match.dto.response.*
import uoslife.servermeeting.match.entity.Match
import uoslife.servermeeting.match.exception.MatchNotFoundException
import uoslife.servermeeting.meetingteam.dao.UserTeamDao
import uoslife.servermeeting.meetingteam.dto.request.CompletionStatus
import uoslife.servermeeting.meetingteam.entity.MeetingTeam
import uoslife.servermeeting.meetingteam.entity.enums.TeamType
import uoslife.servermeeting.meetingteam.entity.enums.TeamType.SINGLE
import uoslife.servermeeting.meetingteam.entity.enums.TeamType.TRIPLE
import uoslife.servermeeting.meetingteam.exception.GenderNotUpdatedException
import uoslife.servermeeting.meetingteam.exception.MeetingTeamNotFoundException
import uoslife.servermeeting.meetingteam.service.impl.SingleMeetingService
import uoslife.servermeeting.meetingteam.service.impl.TripleMeetingService
import uoslife.servermeeting.payment.entity.enums.PaymentStatus
import uoslife.servermeeting.payment.exception.PaymentNotFoundException
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
    @Cacheable(
        value = ["meeting-participation"],
        key = "#season + ':' + #userId",
    )
    fun getUserMeetingParticipation(userId: Long, season: Int): MeetingParticipationResponse {
        return matchedDao.findUserParticipation(userId, season)
    }

    @Cacheable(
        value = ["match-info"],
        key = "#season + ':' + #teamType + ':' + #userId",
    )
    fun getMatchInfo(userId: Long, teamType: TeamType, season: Int): MatchInfoResponse {
        val userTeam =
            userTeamDao.findUserWithTeamTypeAndSeason(userId, teamType, season)
                ?: throw MeetingTeamNotFoundException()
        val meetingTeam = userTeam.team

        val hasInvalidPayment =
            meetingTeam.payments?.any { payment -> payment.status != PaymentStatus.SUCCESS }
                ?: false
        if (hasInvalidPayment) {
            throw PaymentNotFoundException()
        }

        try {
            val match = getMatchByGender(userTeam.user, meetingTeam)
            val opponentTeam = getOpponentTeamByGender(userTeam.user, match)
            val opponentUser = getOpponentLeaderUser(opponentTeam)
            return MatchInfoResponse.toMatchInfoResponse(
                getOpponentUserInformationByTeamType(meetingTeam, opponentUser)
            )
        } catch (e: MatchNotFoundException) {
            return MatchInfoResponse(false, null)
        }
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
}
