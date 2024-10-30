package uoslife.servermeeting.match.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uoslife.servermeeting.match.dao.MatchedDao
import uoslife.servermeeting.meetingteam.service.impl.SingleMeetingService
import uoslife.servermeeting.meetingteam.service.impl.TripleMeetingService
import uoslife.servermeeting.user.dao.UserDao

@Service
@Transactional(readOnly = true)
class MatchingService(
    private val matchedDao: MatchedDao,
    private val userDao: UserDao,
    private val singleMeetingService: SingleMeetingService,
    private val tripleMeetingService: TripleMeetingService,
) {
    //    @Transactional
    //    fun getMatchedMeetingTeam(userId: Long): MatchInformationResponse {
    //        val user = userDao.findUserWithMeetingTeam(userId) ?: throw UserNotFoundException()
    //        val meetingTeam = user.team ?: throw MeetingTeamNotFoundException()
    //
    //        if (!isLeader(user)) throw OnlyTeamLeaderCanGetMatchException()
    //
    //        val match = getMatchByGender(user, meetingTeam)
    //        val opponentTeam = getOpponentTeamByGender(user, match)
    //        val opponentUser = opponentTeam.leader ?: throw UserNotFoundException()
    //
    //        return MatchInformationResponse(
    //            myName = user.name,
    //            getOpponentUserInformationByTeamType(meetingTeam, opponentUser)
    //        )
    //    }
    //
    //    fun getMatchByGender(user: User, meetingTeam: MeetingTeam): Match {
    //        return when (user.userAdditionInformation.gender) {
    //            GenderType.MALE -> matchedDao.findMatchByMaleTeamWithFemaleTeam(meetingTeam)
    //                    ?: throw MatchNotFoundException()
    //            GenderType.FEMALE -> matchedDao.findMatchByFeMaleTeamWithMaleTeam(meetingTeam)
    //                    ?: throw MatchNotFoundException()
    //        }
    //    }
    //
    //    fun getOpponentTeamByGender(user: User, match: Match): MeetingTeam {
    //        return when (user.userAdditionInformation.gender) {
    //            GenderType.MALE -> match.femaleTeam
    //            GenderType.FEMALE -> match.maleTeam
    //        }
    //    }
    //
    //    fun getOpponentUserInformationByTeamType(
    //        meetingTeam: MeetingTeam,
    //        opponentUser: User
    //    ): MatchedMeetingTeamInformationGetResponse {
    //        val meetingTeamInformationGetResponse =
    //            when (meetingTeam.type) {
    //                SINGLE -> {
    //                    singleMeetingService.getMeetingTeamInformation(opponentUser.id!!)
    //                }
    //                TRIPLE -> {
    //                    tripleMeetingService.getMeetingTeamInformation(opponentUser.id!!)
    //                }
    //            }
    //        return meetingTeamInformationGetResponse.toMatchedMeetingTeamInformationGetResponse()
    //    }
    //
    //    private fun isLeader(user: User): Boolean {
    //        if (user.team!!.leader!!.id == user.id) return true
    //        return false
    //    }
}
