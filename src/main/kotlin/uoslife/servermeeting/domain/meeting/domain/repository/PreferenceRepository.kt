package uoslife.servermeeting.domain.meeting.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import uoslife.servermeeting.domain.meeting.domain.entity.MeetingTeam
import uoslife.servermeeting.domain.meeting.domain.entity.Preference

interface PreferenceRepository : JpaRepository<Preference, Long> {
    fun findByMeetingTeam(meetingTeam: MeetingTeam): Preference?
    fun deleteByMeetingTeam(meetingTeam: MeetingTeam)
}
