package uoslife.servermeeting.user.dao

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import uoslife.servermeeting.payment.entity.Payment
import uoslife.servermeeting.payment.entity.QPayment.payment
import uoslife.servermeeting.payment.entity.enums.PaymentStatus
import uoslife.servermeeting.user.command.UserCommand
import uoslife.servermeeting.user.entity.QUser.user
import uoslife.servermeeting.user.entity.QUserInformation.userInformation
import uoslife.servermeeting.user.entity.User

@Repository
class UserDao(
    private val queryFactory: JPAQueryFactory,
) {

    fun findNotMatchedPayment(userIdList: List<Long>): List<Payment> {
        return queryFactory
            .selectFrom(payment)
            //            .innerJoin(payment.user, user)
            .fetchJoin()
            .where(payment.status.eq(PaymentStatus.SUCCESS).and(user.id.notIn(userIdList)))
            .fetch()
    }

    fun findUserProfile(userId: Long): User? {
        return queryFactory
            .selectFrom(user)
            .leftJoin(user.userInformation, userInformation)
            .fetchJoin()
            .where(user.id.eq(userId))
            .fetchOne()
    }

    fun updateUserPersonalInformation(command: UserCommand.UpdateUserPersonalInformation): Long {
        val jpaClause = queryFactory.update(user).where(user.id.eq(command.userId))
        command.name?.let { jpaClause.set(user.name, it) }
        command.phoneNumber?.let { jpaClause.set(user.phoneNumber, it) }
        command.kakaoTalkId?.let { jpaClause.set(user.kakaoTalkId, it) }
        return jpaClause.execute()
    }
}
