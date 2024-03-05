package uoslife.servermeeting.match.repository

import org.springframework.data.jpa.repository.JpaRepository
import uoslife.servermeeting.match.entity.Match
import uoslife.servermeeting.meetingteam.entity.MeetingTeam

interface MatchRepository : JpaRepository<Match, Long> {
    fun existsByFemaleTeam(female: MeetingTeam): Boolean
    fun findByFemaleTeam(female: MeetingTeam): Match?
}
