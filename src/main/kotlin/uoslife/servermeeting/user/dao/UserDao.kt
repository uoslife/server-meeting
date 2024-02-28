package uoslife.servermeeting.user.dao

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import uoslife.servermeeting.match.entity.QMatch.match
import uoslife.servermeeting.meetingteam.entity.QMeetingTeam.meetingTeam
import uoslife.servermeeting.meetingteam.entity.enums.PaymentStatus
import uoslife.servermeeting.user.entity.QUser.user
import uoslife.servermeeting.user.entity.User

@Repository
class UserDao(
    private val queryFactory: JPAQueryFactory,
) {
    fun findNotMatchedUser(): List<User> {
        return queryFactory
            .selectFrom(user)
            .leftJoin(match.maleTeam, meetingTeam)
            .fetchJoin()
            .leftJoin(match.femaleTeam, meetingTeam)
            .fetchJoin()
            .where(match.id.isNull)
            .leftJoin(user.team, meetingTeam)
            .fetchJoin()
            .where(user.payment.status.eq(PaymentStatus.SUCCESS))
            .fetch()
    }
}
