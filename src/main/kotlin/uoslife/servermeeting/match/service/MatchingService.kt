package uoslife.servermeeting.match.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uoslife.servermeeting.match.dao.MatchedDao
import uoslife.servermeeting.match.dto.response.MatchInformationResponse
import uoslife.servermeeting.match.entity.Match
import uoslife.servermeeting.match.exception.MatchNotFoundException
import uoslife.servermeeting.meetingteam.dto.response.MeetingTeamInformationGetResponse
import uoslife.servermeeting.meetingteam.entity.MeetingTeam
import uoslife.servermeeting.meetingteam.entity.enums.TeamType.SINGLE
import uoslife.servermeeting.meetingteam.entity.enums.TeamType.TRIPLE
import uoslife.servermeeting.meetingteam.exception.MeetingTeamNotFoundException
import uoslife.servermeeting.meetingteam.service.impl.SingleMeetingService
import uoslife.servermeeting.meetingteam.service.impl.TripleMeetingService
import uoslife.servermeeting.user.dao.UserDao
import uoslife.servermeeting.user.entity.enums.GenderType
import uoslife.servermeeting.user.entity.User
import uoslife.servermeeting.user.exception.UserNotFoundException

@Service
@Transactional(readOnly = true)
class MatchingService(
    private val userDao: UserDao,
    private val matchedDao: MatchedDao,
    private val singleMeetingService: SingleMeetingService,
    private val tripleMeetingService: TripleMeetingService,
) {
    @Transactional
    fun getMatchedMeetingTeam(userId: Long): MatchInformationResponse {
        val user = userDao.findUserWithMeetingTeam(userId) ?: throw UserNotFoundException()
        val meetingTeam = user.team ?: throw MeetingTeamNotFoundException()

        val match = getMatchByGender(user, meetingTeam)

        val opponentTeam = getOpponentTeamByGender(user, match)

        val opponentUser = opponentTeam.leader ?: throw UserNotFoundException()

        return MatchInformationResponse(
            myName = user.name,
            getOpponentUserInformationByTeamType(meetingTeam, opponentUser)
        )
    }

    fun getMatchByGender(user: User, meetingTeam: MeetingTeam): Match {
        return when (user.userPersonalInformation.gender) {
            GenderType.MALE -> matchedDao.findMatchByMaleTeamWithFemaleTeam(meetingTeam)
                    ?: throw MatchNotFoundException()
            GenderType.FEMALE -> matchedDao.findMatchByFeMaleTeamWithMaleTeam(meetingTeam)
                    ?: throw MatchNotFoundException()
        }
    }

    fun getOpponentTeamByGender(user: User, match: Match): MeetingTeam {
        return when (user.userPersonalInformation.gender) {
            GenderType.MALE -> match.femaleTeam
            GenderType.FEMALE -> match.maleTeam
        }
    }

    fun getOpponentUserInformationByTeamType(
        meetingTeam: MeetingTeam,
        opponentUser: User
    ): MeetingTeamInformationGetResponse {
        return when (meetingTeam.type) {
            SINGLE -> {
                singleMeetingService.getMeetingTeamInformation(opponentUser.id!!)
            }
            TRIPLE -> {
                tripleMeetingService.getMeetingTeamInformation(opponentUser.id!!)
            }
        }
    }
}
