package uoslife.servermeeting.meetingteam.dao

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import uoslife.servermeeting.match.entity.QMatch
import uoslife.servermeeting.meetingteam.entity.MeetingTeam
import uoslife.servermeeting.meetingteam.entity.QMeetingTeam.meetingTeam
import uoslife.servermeeting.meetingteam.entity.enums.TeamType
import uoslife.servermeeting.user.entity.User

@Repository
class MeetingTeamDao(
    private val queryFactory: JPAQueryFactory,
) {

    fun findByUserWithMeetingTeam(user: User, teamType: TeamType): MeetingTeam? {
        return queryFactory
            .selectFrom(meetingTeam)
            .where(meetingTeam.leader.eq(user))
            .where(meetingTeam.type.eq(teamType))
            .fetchOne()
    }

    fun findNotMatchedMaleMeetingTeam(): List<MeetingTeam> {
        return queryFactory
            .selectFrom(meetingTeam)
            .leftJoin(meetingTeam.maleMatch, QMatch.match)
            .fetchJoin()
            .where(QMatch.match.id.isNull)
            .fetch()
    }

    fun findNotMatchedFeMaleMeetingTeam(): List<MeetingTeam> {
        return queryFactory
            .selectFrom(meetingTeam)
            .leftJoin(meetingTeam.femaleMatch, QMatch.match)
            .fetchJoin()
            .where(QMatch.match.id.isNull)
            .fetch()
    }
}
