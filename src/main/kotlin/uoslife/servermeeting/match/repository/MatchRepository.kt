package uoslife.servermeeting.match.repository

import org.springframework.data.jpa.repository.JpaRepository
import uoslife.servermeeting.match.entity.Match

interface MatchRepository : JpaRepository<Match, Long> {

}