package uoslife.servermeeting.domain.meeting.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import uoslife.servermeeting.domain.meeting.domain.entity.Information
import uoslife.servermeeting.domain.meeting.domain.entity.MeetingTeam

interface InformationRepository : JpaRepository<Information, Long> {
    fun findByMeetingTeam(meetingTeam: MeetingTeam): Information?
    fun deleteByMeetingTeam(meetingTeam: MeetingTeam)
}
