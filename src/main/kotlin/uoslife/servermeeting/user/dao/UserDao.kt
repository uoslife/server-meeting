package uoslife.servermeeting.user.dao

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import uoslife.servermeeting.meetingteam.entity.Payment
import uoslife.servermeeting.meetingteam.entity.QMeetingTeam.meetingTeam
import uoslife.servermeeting.meetingteam.entity.QPayment.payment
import uoslife.servermeeting.meetingteam.entity.QUserTeam.userTeam
import uoslife.servermeeting.meetingteam.entity.UserTeam
import uoslife.servermeeting.meetingteam.entity.enums.PaymentStatus
import uoslife.servermeeting.meetingteam.entity.enums.TeamType
import uoslife.servermeeting.user.entity.QUser.user
import uoslife.servermeeting.user.entity.User

@Repository
class UserDao(
    private val queryFactory: JPAQueryFactory,
) {
    fun findByUserWithMeetingTeam(user: User, teamType: TeamType): UserTeam? {
        return queryFactory
            .selectFrom(userTeam)
            .join(userTeam.team)
            .fetchJoin()
            .where(userTeam.user.eq(user))
            .where(meetingTeam.type.eq(teamType))
            .fetchOne()
    }
    fun findNotMatchedPayment(userIdList: List<Long>): List<Payment> {
        return queryFactory
            .selectFrom(payment)
            //            .innerJoin(payment.user, user)
            .fetchJoin()
            .where(payment.status.eq(PaymentStatus.SUCCESS).and(user.id.notIn(userIdList)))
            .fetch()
    }
}
