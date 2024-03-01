package uoslife.servermeeting.user.dao

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import uoslife.servermeeting.match.entity.QMatch.match
import uoslife.servermeeting.meetingteam.entity.MeetingTeam
import uoslife.servermeeting.meetingteam.entity.QMeetingTeam.meetingTeam
import uoslife.servermeeting.meetingteam.entity.enums.PaymentStatus
import uoslife.servermeeting.user.entity.QUser.user
import uoslife.servermeeting.user.entity.User

@Repository
class UserDao(
    private val queryFactory: JPAQueryFactory,
) {
    fun findNotMatchedMaleMeetingTeam(): List<MeetingTeam> {
        return queryFactory
            .selectFrom(meetingTeam)
            .leftJoin(meetingTeam.maleMatch, match)
            .fetchJoin()
            .where(match.id.isNull)
            .fetch()
    }

    fun findNotMatchedFeMaleMeetingTeam(): List<MeetingTeam> {
        return queryFactory
            .selectFrom(meetingTeam)
            .leftJoin(meetingTeam.femaleMatch, match)
            .fetchJoin()
            .where(match.id.isNull)
            .fetch()
    }

    fun findNotMatchedUserInMeetingTeam(meetingTeamList: List<MeetingTeam>): List<User> {
        return queryFactory
            .selectFrom(user)
            .leftJoin(user.team, meetingTeam)
            .fetchJoin()
            .where(user.payment.status.eq(PaymentStatus.SUCCESS)
                .and(meetingTeam.`in`(meetingTeamList)))
            .fetch()
    }
}
