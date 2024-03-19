package uoslife.servermeeting.match.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uoslife.servermeeting.match.dao.MatchedDao
import uoslife.servermeeting.match.exception.MatchNotFoundException
import uoslife.servermeeting.meetingteam.dto.response.MeetingTeamInformationGetResponse
import uoslife.servermeeting.meetingteam.entity.enums.TeamType.SINGLE
import uoslife.servermeeting.meetingteam.entity.enums.TeamType.TRIPLE
import uoslife.servermeeting.meetingteam.exception.MeetingTeamNotFoundException
import uoslife.servermeeting.meetingteam.service.impl.SingleMeetingService
import uoslife.servermeeting.meetingteam.service.impl.TripleMeetingService
import uoslife.servermeeting.user.dao.UserDao
import uoslife.servermeeting.user.entity.enums.GenderType
import uoslife.servermeeting.user.exception.UserNotFoundException
import java.util.*

@Service
@Transactional(readOnly = true)
class MatchingService(
    private val userDao: UserDao,
    private val matchedDao: MatchedDao,
    private val singleMeetingService: SingleMeetingService,
    private val tripleMeetingService: TripleMeetingService,
) {
    @Transactional
    fun getMatchedMeetingTeam(userUUID: UUID): MeetingTeamInformationGetResponse {
        val user = userDao.findUserWithMeetingTeam(userUUID) ?: throw UserNotFoundException()
        val meetingTeam = user.team ?: throw MeetingTeamNotFoundException()

        val match =
            when (user.userPersonalInformation.gender) {
                GenderType.MALE -> matchedDao.findMatchByMaleTeamWithFemaleTeam(meetingTeam)
                        ?: throw MatchNotFoundException()
                GenderType.FEMALE -> matchedDao.findMatchByFeMaleTeamWithMaleTeam(meetingTeam)
                        ?: throw MatchNotFoundException()
            }
        val opponentTeam =
            when (user.userPersonalInformation.gender) {
                GenderType.MALE -> match.femaleTeam
                GenderType.FEMALE -> match.maleTeam
            }

        val opponentUser = opponentTeam.leader ?: throw UserNotFoundException()

        return when (meetingTeam.type) {
            SINGLE -> {
                singleMeetingService.getMeetingTeamInformation(
                    UUID.fromString(opponentUser.id.toString()) ?: throw UserNotFoundException()
                )
            }
            TRIPLE -> {
                tripleMeetingService.getMeetingTeamInformation(
                    UUID.fromString(opponentUser.id.toString()) ?: throw UserNotFoundException()
                )
            }
        }
    }
}
