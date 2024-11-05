package uoslife.servermeeting.meetingteam.repository

import org.springframework.data.jpa.repository.JpaRepository
import uoslife.servermeeting.meetingteam.entity.UserTeam

interface UserTeamRepository : JpaRepository<UserTeam, Long> {
}
