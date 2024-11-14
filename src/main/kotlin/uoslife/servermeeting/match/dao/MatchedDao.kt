package uoslife.servermeeting.match.dao

import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.transaction.Transactional
import org.springframework.stereotype.Repository
import uoslife.servermeeting.match.entity.Match
import uoslife.servermeeting.match.entity.QMatch.match
import uoslife.servermeeting.meetingteam.entity.MeetingTeam
import uoslife.servermeeting.meetingteam.entity.QMeetingTeam.meetingTeam
import uoslife.servermeeting.meetingteam.entity.QUserTeam.userTeam

@Repository
@Transactional
class MatchedDao(private val queryFactory: JPAQueryFactory) {
    fun findMatchByMaleTeamWithFemaleTeam(maleTeam: MeetingTeam): Match? {
        return queryFactory
            .selectFrom(match)
            .join(match.femaleTeam, meetingTeam)
            .join(meetingTeam.userTeams, userTeam)
            .where(match.maleTeam.eq(maleTeam).and(userTeam.isLeader.eq(true)))
            .fetchOne()
    }

    fun findMatchByFeMaleTeamWithMaleTeam(femaleTeam: MeetingTeam): Match? {
        return queryFactory
            .selectFrom(match)
            .join(match.maleTeam, meetingTeam)
            .join(meetingTeam.userTeams, userTeam)
            .where(match.femaleTeam.eq(femaleTeam).and(userTeam.isLeader.eq(true)))
            .fetchOne()
    }
}
