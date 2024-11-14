package uoslife.servermeeting.meetingteam.dao

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import uoslife.servermeeting.meetingteam.entity.Payment
import uoslife.servermeeting.meetingteam.entity.QMeetingTeam.meetingTeam
import uoslife.servermeeting.meetingteam.entity.QPayment.payment
import uoslife.servermeeting.meetingteam.entity.QUserTeam.userTeam
import uoslife.servermeeting.meetingteam.entity.enums.TeamType
import uoslife.servermeeting.user.entity.User

@Repository
class PaymentDao(private val queryFactory: JPAQueryFactory) {
    fun getPaymentWithUserAndTeamType(user: User, teamType: TeamType): Payment? {
        return when (teamType) {
            TeamType.SINGLE ->
                queryFactory
                    .selectFrom(payment)
                    .join(meetingTeam)
                    .join(userTeam)
                    .on(userTeam.user.eq(user))
                    .where(meetingTeam.type.eq(TeamType.SINGLE))
                    .fetchOne()
            TeamType.TRIPLE ->
                queryFactory
                    .selectFrom(payment)
                    .join(meetingTeam)
                    .join(userTeam)
                    .on(userTeam.user.eq(user))
                    .where(meetingTeam.type.eq(TeamType.TRIPLE))
                    .fetchOne()
        }
    }
}
