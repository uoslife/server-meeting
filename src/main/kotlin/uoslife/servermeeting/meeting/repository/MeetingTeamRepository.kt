package uoslife.servermeeting.meeting.repository

import org.springframework.data.jpa.repository.JpaRepository
import uoslife.servermeeting.meeting.entity.MeetingTeam

interface MeetingTeamRepository : JpaRepository<MeetingTeam, Long> {

}