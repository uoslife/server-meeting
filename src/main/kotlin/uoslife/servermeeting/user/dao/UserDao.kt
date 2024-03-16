package uoslife.servermeeting.user.dao

import com.querydsl.jpa.impl.JPAQueryFactory
import java.util.*
import org.springframework.stereotype.Repository
import uoslife.servermeeting.meetingteam.entity.MeetingTeam
import uoslife.servermeeting.meetingteam.entity.QMeetingTeam.meetingTeam
import uoslife.servermeeting.meetingteam.entity.enums.PaymentStatus
import uoslife.servermeeting.user.entity.QUser.user
import uoslife.servermeeting.user.entity.User

@Repository
class UserDao(
    private val queryFactory: JPAQueryFactory,
) {
    fun findUserWithMeetingTeam(userId: UUID): User? {
        return queryFactory
            .selectFrom(user)
            .leftJoin(user.team, meetingTeam)
            .fetchJoin()
            .where(user.id.eq(userId))
            .fetchOne()
    }
    fun findNotMatchedUserInMeetingTeam(meetingTeamList: List<MeetingTeam>): List<User> {
        return queryFactory
            .selectFrom(user)
            .leftJoin(user.team, meetingTeam)
            .fetchJoin()
            .where(
                user.payment.status.eq(PaymentStatus.SUCCESS).and(meetingTeam.`in`(meetingTeamList))
            )
            .fetch()
    }
}
