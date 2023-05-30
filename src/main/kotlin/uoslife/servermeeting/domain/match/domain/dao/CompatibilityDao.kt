package uoslife.servermeeting.domain.match.domain.dao

import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.transaction.Transactional
import org.springframework.stereotype.Repository
import uoslife.servermeeting.domain.match.domain.entity.QCompatibility.compatibility
import uoslife.servermeeting.domain.meeting.domain.entity.MeetingTeam

@Repository
@Transactional
class CompatibilityDao(
    private val jpaQueryFactory: JPAQueryFactory,
) {

    fun findCompatibilityScoreByTeam(team: MeetingTeam, opponentTeam: MeetingTeam): Int {
        return jpaQueryFactory.selectFrom(compatibility)
            .where(compatibility.maleTeam.eq(team), compatibility.femaleTeam.eq(opponentTeam))
            .fetchOne()?.score ?: 0
    }

    fun saveCompatibilityScore(team: MeetingTeam, opponentTeam: MeetingTeam, score: Int) {
        jpaQueryFactory.insert(compatibility)
            .set(compatibility.maleTeam, team)
            .set(compatibility.femaleTeam, opponentTeam)
            .set(compatibility.score, score)
            .execute()
    }
}
