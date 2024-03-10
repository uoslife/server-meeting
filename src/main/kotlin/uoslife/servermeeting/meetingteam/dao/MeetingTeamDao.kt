package uoslife.servermeeting.meetingteam.dao

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import uoslife.servermeeting.match.entity.QMatch
import uoslife.servermeeting.meetingteam.entity.MeetingTeam
import uoslife.servermeeting.meetingteam.entity.QMeetingTeam.meetingTeam
import uoslife.servermeeting.meetingteam.entity.enums.TeamType
import uoslife.servermeeting.user.entity.QUser.user
import uoslife.servermeeting.user.entity.User
import java.util.UUID

@Repository
class MeetingTeamDao(
    private val queryFactory: JPAQueryFactory,
) {
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
