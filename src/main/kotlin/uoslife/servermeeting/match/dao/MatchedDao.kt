package uoslife.servermeeting.match.dao

import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.transaction.Transactional
import org.springframework.stereotype.Repository
import uoslife.servermeeting.match.dto.MatchResultDto
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

    fun findByTeam(team: MeetingTeam): Match? {
        return queryFactory
            .selectFrom(match)
            .leftJoin(match.maleTeam, meetingTeam)
            .leftJoin(match.femaleTeam, meetingTeam)
            .where(match.maleTeam.eq(team).or(match.femaleTeam.eq(team)))
            .fetchOne()
    }

    fun findMatchResultByUserIdAndTeamId(userId: Long, teamId: Long): MatchResultDto? {
        return queryFactory
            .select(Projections.constructor(MatchResultDto::class.java, meetingTeam.type, match.id))
            .from(userTeam)
            .join(userTeam.team, meetingTeam)
            .leftJoin(match)
            .on(meetingTeam.eq(match.maleTeam).or(meetingTeam.eq(match.femaleTeam)))
            .where(userTeam.user.id.eq(userId), userTeam.team.id.eq(teamId))
            .fetchOne()
    }

    fun findById(matchId: Long): Match? {
        return queryFactory
            .selectFrom(match)
            .join(match.maleTeam)
            .fetchJoin()
            .join(match.femaleTeam)
            .fetchJoin()
            .leftJoin(match.maleTeam.userTeams)
            .fetchJoin()
            .leftJoin(match.femaleTeam.userTeams)
            .fetchJoin()
            .where(match.id.eq(matchId))
            .fetchOne()
    }
}
