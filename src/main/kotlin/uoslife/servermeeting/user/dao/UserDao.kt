package uoslife.servermeeting.user.dao

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import uoslife.servermeeting.meetingteam.entity.Payment
import uoslife.servermeeting.meetingteam.entity.QPayment.payment
import uoslife.servermeeting.meetingteam.entity.QSingleMeetingTeam.singleMeetingTeam
import uoslife.servermeeting.meetingteam.entity.QTripleMeetingTeam.tripleMeetingTeam
import uoslife.servermeeting.meetingteam.entity.enums.PaymentStatus
import uoslife.servermeeting.user.entity.QUser.user
import uoslife.servermeeting.user.entity.User

@Repository
class UserDao(
    private val queryFactory: JPAQueryFactory,
) {
    fun findUserWithMeetingTeam(userId: Long): User? {
        return queryFactory
            .selectFrom(user)
            .leftJoin(user.singleTeam, singleMeetingTeam)
            .leftJoin(user.tripleTeam, tripleMeetingTeam)
            .fetchJoin()
            .where(user.id.eq(userId))
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
