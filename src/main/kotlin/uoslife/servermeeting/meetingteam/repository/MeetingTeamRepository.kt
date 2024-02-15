package uoslife.servermeeting.meetingteam.repository

import org.springframework.data.jpa.repository.JpaRepository
import uoslife.servermeeting.meetingteam.entity.MeetingTeam

interface MeetingTeamRepository : JpaRepository<MeetingTeam, Long> {
    fun existsByCode(code: String): Boolean
    fun findByCode(code: String): MeetingTeam?
}
