package uoslife.servermeeting.match.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uoslife.servermeeting.match.dao.MatchedDao
import uoslife.servermeeting.match.dto.response.MatchInformationResponse
import uoslife.servermeeting.match.dto.response.MatchedMeetingTeamInformationGetResponse
import uoslife.servermeeting.match.entity.Match
import uoslife.servermeeting.match.exception.MatchNotFoundException
import uoslife.servermeeting.meetingteam.dao.UserTeamDao
import uoslife.servermeeting.meetingteam.entity.MeetingTeam
import uoslife.servermeeting.meetingteam.entity.enums.TeamType
import uoslife.servermeeting.meetingteam.entity.enums.TeamType.SINGLE
import uoslife.servermeeting.meetingteam.entity.enums.TeamType.TRIPLE
import uoslife.servermeeting.meetingteam.exception.GenderNotUpdatedException
import uoslife.servermeeting.meetingteam.exception.MeetingTeamNotFoundException
import uoslife.servermeeting.meetingteam.exception.OnlyTeamLeaderCanGetMatchException
import uoslife.servermeeting.meetingteam.service.impl.SingleMeetingService
import uoslife.servermeeting.meetingteam.service.impl.TripleMeetingService
import uoslife.servermeeting.user.dao.UserDao
import uoslife.servermeeting.user.entity.User
import uoslife.servermeeting.user.entity.enums.GenderType
import uoslife.servermeeting.user.exception.UserNotFoundException

@Service
@Transactional(readOnly = true)
class MatchingService(
    private val matchedDao: MatchedDao,
    private val userTeamDao: UserTeamDao,
    private val userDao: UserDao,
    private val singleMeetingService: SingleMeetingService,
    private val tripleMeetingService: TripleMeetingService,
) {
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
                    singleMeetingService.getMeetingTeamInformation(opponentUser.id!!)
                }
                TRIPLE -> {
                    tripleMeetingService.getMeetingTeamInformation(opponentUser.id!!)
                }
            }
        return meetingTeamInformationGetResponse.toMatchedMeetingTeamInformationGetResponse()
    }
}
