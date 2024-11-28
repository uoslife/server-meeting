package uoslife.servermeeting.payment.dao

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import uoslife.servermeeting.meetingteam.entity.enums.TeamType
import uoslife.servermeeting.payment.entity.Payment
import uoslife.servermeeting.payment.entity.QPayment.payment
import uoslife.servermeeting.payment.entity.enums.PaymentStatus
import uoslife.servermeeting.user.entity.QUser.user
import uoslife.servermeeting.user.entity.User

@Repository
class PaymentDao(private val queryFactory: JPAQueryFactory) {
    fun getAllPaymentWithUserAndTeamType(requestUser: User, teamType: TeamType): List<Payment>? {
        return queryFactory
            .selectFrom(payment)
            .join(payment.user, user)
            .fetchJoin() // 관계 명시 및 fetchJoin 적용
            .where(payment.teamType.eq(teamType).and(user.eq(requestUser)))
            .fetch()
    }

    fun getSuccessPaymentFromUserIdAndTeamType(userId: Long, teamType: TeamType): Payment? {
        return queryFactory
            .selectFrom(payment)
            .where(
                payment.user.id
                    .eq(userId)
                    .and(payment.teamType.eq(teamType))
                    .and(payment.status.eq(PaymentStatus.SUCCESS))
            )
            .fetchOne()
    }

    fun getPendingPaymentFromUserIdAndTeamType(userId: Long, teamType: TeamType): Payment? {
        return queryFactory
            .selectFrom(payment)
            .where(
                payment.user.id
                    .eq(userId)
                    .and(payment.teamType.eq(teamType))
                    .and(payment.status.eq(PaymentStatus.PENDING))
            )
            .fetchOne()
    }
}
