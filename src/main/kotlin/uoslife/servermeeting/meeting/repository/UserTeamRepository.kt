package uoslife.servermeeting.meeting.repository

import org.springframework.data.jpa.repository.JpaRepository
import uoslife.servermeeting.meeting.entity.UserTeam

interface UserTeamRepository : JpaRepository<UserTeam, Long> {

}