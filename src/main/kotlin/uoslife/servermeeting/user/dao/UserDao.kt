package uoslife.servermeeting.user.dao

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import uoslife.servermeeting.meetingteam.entity.Payment
import uoslife.servermeeting.meetingteam.entity.QPayment.payment
import uoslife.servermeeting.meetingteam.entity.QUserTeam.userTeam
import uoslife.servermeeting.meetingteam.entity.UserTeam
import uoslife.servermeeting.meetingteam.entity.enums.PaymentStatus
import uoslife.servermeeting.meetingteam.entity.enums.TeamType
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
            .join(userInformation)
            .on(userInformation.user.eq(user))
            .where(user.id.eq(userId))
            .fetchJoin()
            .fetchOne()
    }

    fun updateUserInformation(command: UserCommand.UpdateUserInformation): Long {

        val jpaClause =
            queryFactory.update(userInformation).where(userInformation.user.id.eq(command.userId))

        command.smoking?.let { jpaClause.set(userInformation.smoking, it) }
        command.mbti?.let { jpaClause.set(userInformation.mbti, it) }
        command.interest?.let { jpaClause.set(userInformation.interest, it) }
        command.height?.let { jpaClause.set(userInformation.height, it) }
        command.age?.let { jpaClause.set(userInformation.age, it) }
        command.studentNumber?.let { jpaClause.set(userInformation.studentNumber, it) }
        command.department?.let { jpaClause.set(userInformation.department, it) }
        command.eyelidType?.let { jpaClause.set(userInformation.eyelidType, it) }
        command.appearanceType?.let { jpaClause.set(userInformation.appearanceType, it) }
        return jpaClause.execute()
    }

    fun updateUserPersonalInformation(command: UserCommand.UpdateUserPersonalInformation): Long {
        val jpaClause = queryFactory.update(user).where(user.id.eq(command.userId))
        command.name?.let { jpaClause.set(user.name, it) }
        command.phoneNumber?.let { jpaClause.set(user.phoneNumber, it) }
        command.gender?.let { jpaClause.set(user.gender, it) }
        command.kakaoTalkId?.let { jpaClause.set(user.kakaoTalkId, it) }
        return jpaClause.execute()
    }
}
