package uoslife.servermeeting.domain.meeting.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import uoslife.servermeeting.domain.meeting.domain.entity.Information
import uoslife.servermeeting.domain.meeting.domain.entity.MeetingTeam

interface MeetingTeamRepository : JpaRepository<MeetingTeam, Long> {
    fun existsByCode(code: String): Boolean
    fun findByCode(code: String): MeetingTeam?
}
