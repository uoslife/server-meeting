package uoslife.servermeeting.match.service

import java.util.UUID
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uoslife.servermeeting.match.dao.MatchedDao
import uoslife.servermeeting.match.exception.MatchNotFoundException
import uoslife.servermeeting.meetingteam.dto.response.MeetingTeamInformationGetResponse
import uoslife.servermeeting.meetingteam.dao.UserTeamDao
import uoslife.servermeeting.meetingteam.entity.enums.TeamType.SINGLE
import uoslife.servermeeting.meetingteam.entity.enums.TeamType.TRIPLE
import uoslife.servermeeting.meetingteam.exception.MeetingTeamNotFoundException
import uoslife.servermeeting.meetingteam.exception.UserTeamNotFoundException
import uoslife.servermeeting.meetingteam.repository.MeetingTeamRepository
import uoslife.servermeeting.meetingteam.service.impl.SingleMeetingService
import uoslife.servermeeting.meetingteam.service.impl.TripleMeetingService
import uoslife.servermeeting.user.exception.UserNotFoundException
import uoslife.servermeeting.user.repository.UserRepository

@Service
@Transactional(readOnly = true)
class MatchingService(
    private val userRepository: UserRepository,
    private val userTeamDao: UserTeamDao,
    private val meetingTeamRepository: MeetingTeamRepository,
    private val matchedDao: MatchedDao,
    private val singleMeetingService: SingleMeetingService,
    private val tripleMeetingService: TripleMeetingService,
) {
    @Transactional
    fun getMatchedMeetingTeam(userUUID: UUID): MeetingTeamInformationGetResponse {
        val user = userRepository.findByIdOrNull(userUUID) ?: throw UserNotFoundException()
        val userTeam = userTeamDao.findByUser(user) ?: throw UserTeamNotFoundException()
        val meetingTeam =
            meetingTeamRepository.findByIdOrNull(userTeam.team.id!!)
                ?: throw MeetingTeamNotFoundException()
        val opponentTeam =
            matchedDao.findMatchedTeamByTeamAndGender(
                meetingTeam,
                user.userPersonalInformation.gender
            )
                ?: throw MatchNotFoundException()
        return when (userTeam.type) {
            SINGLE -> {
                val opponentUserTeam = userTeamDao.findByTeam(opponentTeam)
                val opponentUser = opponentUserTeam.first().user ?: throw UserNotFoundException()
                singleMeetingService.getMeetingTeamInformation(
                    opponentUser.id ?: throw UserNotFoundException()
                )
            }
            TRIPLE -> {
                val opponentUserTeam = userTeamDao.findByTeam(opponentTeam)
                val opponentUser = opponentUserTeam.first().user ?: throw UserNotFoundException()
                tripleMeetingService.getMeetingTeamInformation(
                    opponentUser.id ?: throw UserNotFoundException()
                )
            }
        }
    }
}
