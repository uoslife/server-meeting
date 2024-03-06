package uoslife.servermeeting.meetingteam.dao

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import uoslife.servermeeting.match.entity.QMatch
import uoslife.servermeeting.meetingteam.entity.MeetingTeam
import uoslife.servermeeting.meetingteam.entity.QMeetingTeam

@Repository
class MeetingTeamDao(
    private val queryFactory: JPAQueryFactory,
) {
    fun findNotMatchedMaleMeetingTeam(): List<MeetingTeam> {
        return queryFactory
            .selectFrom(QMeetingTeam.meetingTeam)
            .leftJoin(QMeetingTeam.meetingTeam.maleMatch, QMatch.match)
            .fetchJoin()
            .where(QMatch.match.id.isNull)
            .fetch()
    }

    fun findNotMatchedFeMaleMeetingTeam(): List<MeetingTeam> {
        return queryFactory
            .selectFrom(QMeetingTeam.meetingTeam)
            .leftJoin(QMeetingTeam.meetingTeam.femaleMatch, QMatch.match)
            .fetchJoin()
            .where(QMatch.match.id.isNull)
            .fetch()
    }
}
