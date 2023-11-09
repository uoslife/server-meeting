package uoslife.servermeeting.domain.match.domain.service.impl

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uoslife.servermeeting.domain.match.domain.dao.MatchedDao
import uoslife.servermeeting.domain.match.domain.exception.MatchNotFoundException
import uoslife.servermeeting.domain.meeting.application.response.MeetingTeamInformationGetResponse
import uoslife.servermeeting.domain.meeting.domain.dao.UserTeamDao
import uoslife.servermeeting.domain.meeting.domain.entity.enums.TeamType.SINGLE
import uoslife.servermeeting.domain.meeting.domain.entity.enums.TeamType.TRIPLE
import uoslife.servermeeting.domain.meeting.domain.exception.MeetingTeamNotFoundException
import uoslife.servermeeting.domain.meeting.domain.exception.UserTeamNotFoundException
import uoslife.servermeeting.domain.meeting.domain.repository.MeetingTeamRepository
import uoslife.servermeeting.domain.meeting.domain.service.impl.SingleMeetingService
import uoslife.servermeeting.domain.meeting.domain.service.impl.TripleMeetingService
import uoslife.servermeeting.domain.user.domain.exception.UserNotFoundException
import uoslife.servermeeting.domain.user.domain.repository.UserRepository
import java.util.UUID

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
            meetingTeamRepository.findByIdOrNull(userTeam.team.id!!) ?: throw MeetingTeamNotFoundException()
        val opponentTeam =
            matchedDao.findMatchedTeamByTeamAndGender(meetingTeam, user.userPersonalInformation.gender)
                ?: throw MatchNotFoundException()
        return when (userTeam.type) {
            SINGLE -> {
                val opponentUserTeam =
                    userTeamDao.findByTeam(opponentTeam)
                val opponentUser = opponentUserTeam.first().user ?: throw UserNotFoundException()
                singleMeetingService.getMeetingTeamInformation(opponentUser.id ?: throw UserNotFoundException())
            }

            TRIPLE -> {
                val opponentUserTeam =
                    userTeamDao.findByTeam(opponentTeam)
                val opponentUser = opponentUserTeam.first().user ?: throw UserNotFoundException()
                tripleMeetingService.getMeetingTeamInformation(opponentUser.id ?: throw UserNotFoundException())
            }
        }
    }
}
