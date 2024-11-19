package uoslife.servermeeting.payment.dao

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import uoslife.servermeeting.meetingteam.entity.QMeetingTeam.meetingTeam
import uoslife.servermeeting.meetingteam.entity.QUserTeam.userTeam
import uoslife.servermeeting.meetingteam.entity.enums.TeamType
import uoslife.servermeeting.payment.entity.Payment
import uoslife.servermeeting.payment.entity.QPayment.payment
import uoslife.servermeeting.payment.entity.enums.PaymentStatus
import uoslife.servermeeting.user.entity.QUser.user
import uoslife.servermeeting.user.entity.User

@Repository
class PaymentDao(private val queryFactory: JPAQueryFactory) {
    fun getPaymentWithUserAndTeamType(user: User, teamType: TeamType): List<Payment>? {
        return when (teamType) {
            TeamType.SINGLE ->
                queryFactory
                    .selectFrom(payment)
                    .join(meetingTeam)
                    .join(userTeam)
                    .on(userTeam.user.eq(user))
                    .where(meetingTeam.type.eq(TeamType.SINGLE))
                    .fetch()
            TeamType.TRIPLE ->
                queryFactory
                    .selectFrom(payment)
                    .join(meetingTeam)
                    .join(userTeam)
                    .on(userTeam.user.eq(user))
                    .where(meetingTeam.type.eq(TeamType.TRIPLE))
                    .fetch()
        }
    }

    fun getSuccessPaymentFromUserIdAndTeamType(userId: Long, teamType: TeamType): Payment? {
        return queryFactory
            .selectFrom(payment)
            .join(meetingTeam)
            .join(user)
            .on(user.id.eq(userId))
            .where(payment.teamType.eq(teamType))
            .where(payment.status.eq(PaymentStatus.SUCCESS))
            .fetchOne()
    }
    fun getPendingPaymentFromUserIdAndTeamType(userId: Long, teamType: TeamType): Payment? {
        return queryFactory
            .selectFrom(payment)
            .join(meetingTeam)
            .join(user)
            .on(user.id.eq(userId))
            .where(payment.teamType.eq(teamType))
            .where(payment.status.eq(PaymentStatus.PENDING))
            .fetchOne()
    }
}
