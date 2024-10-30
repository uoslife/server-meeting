package uoslife.servermeeting.meetingteam.repository

import org.springframework.data.jpa.repository.JpaRepository
import uoslife.servermeeting.meetingteam.entity.SingleMeetingTeam

interface SingleMeetingTeamRepository : JpaRepository<SingleMeetingTeam, Long> {}
