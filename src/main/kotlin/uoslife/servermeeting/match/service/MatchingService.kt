package uoslife.servermeeting.match.service

import java.util.*
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uoslife.servermeeting.match.entity.QMatch.match
import uoslife.servermeeting.match.exception.MatchNotFoundException
import uoslife.servermeeting.meetingteam.dto.response.MeetingTeamInformationGetResponse
import uoslife.servermeeting.meetingteam.entity.enums.TeamType.SINGLE
import uoslife.servermeeting.meetingteam.entity.enums.TeamType.TRIPLE
import uoslife.servermeeting.meetingteam.exception.MeetingTeamNotFoundException
import uoslife.servermeeting.meetingteam.service.impl.SingleMeetingService
import uoslife.servermeeting.meetingteam.service.impl.TripleMeetingService
import uoslife.servermeeting.user.entity.enums.GenderType
import uoslife.servermeeting.user.exception.UserNotFoundException
import uoslife.servermeeting.user.repository.UserRepository

@Service
@Transactional(readOnly = true)
class MatchingService(
    private val userRepository: UserRepository,
    private val singleMeetingService: SingleMeetingService,
    private val tripleMeetingService: TripleMeetingService,
) {
    @Transactional
    fun getMatchedMeetingTeam(userUUID: UUID): MeetingTeamInformationGetResponse {
        val user = userRepository.findByIdOrNull(userUUID) ?: throw UserNotFoundException()
        val meetingTeam = user.team ?: throw MeetingTeamNotFoundException()
        val opponentTeam =
            when (user.userPersonalInformation.gender) {
                GenderType.MALE -> match.femaleTeam
                GenderType.FEMALE -> match.maleTeam
            }
                ?: throw MatchNotFoundException()
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
