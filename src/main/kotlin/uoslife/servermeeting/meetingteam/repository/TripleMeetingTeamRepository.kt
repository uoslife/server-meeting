package uoslife.servermeeting.meetingteam.repository

import org.springframework.data.jpa.repository.JpaRepository
import uoslife.servermeeting.meetingteam.entity.TripleMeetingTeam

interface TripleMeetingTeamRepository : JpaRepository<TripleMeetingTeam, Long> {
    fun existsByCode(code: String): Boolean
    fun findByCode(code: String): TripleMeetingTeam?
}
