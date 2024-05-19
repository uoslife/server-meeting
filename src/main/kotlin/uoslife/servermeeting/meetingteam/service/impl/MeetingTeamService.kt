package uoslife.servermeeting.meetingteam.service.impl

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uoslife.servermeeting.meetingteam.entity.MeetingTeam
import uoslife.servermeeting.meetingteam.repository.MeetingTeamRepository

@Service
@Transactional(readOnly = true)
class MeetingTeamService(
    val meetingTeamRepository: MeetingTeamRepository,
) {

    @Transactional
    fun deleteEmptyMeetingTeam(
        meetingTeam: MeetingTeam,
    ) {
        if (meetingTeam.users.size <= 1) {
            meetingTeamRepository.delete(meetingTeam)
        }
    }
}
