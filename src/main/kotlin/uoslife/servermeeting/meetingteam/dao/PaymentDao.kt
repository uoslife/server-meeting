package uoslife.servermeeting.meetingteam.dao

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import uoslife.servermeeting.meetingteam.entity.Payment
import uoslife.servermeeting.meetingteam.entity.QPayment.payment
import uoslife.servermeeting.meetingteam.entity.QSingleMeetingTeam.singleMeetingTeam
import uoslife.servermeeting.meetingteam.entity.QTripleMeetingTeam.tripleMeetingTeam
import uoslife.servermeeting.meetingteam.entity.enums.TeamType
import uoslife.servermeeting.user.entity.User

@Repository
class PaymentDao(private val queryFactory: JPAQueryFactory) {
    fun getPaymentWithUserAndTeamType(user: User, teamType: TeamType): Payment? {
        return when (teamType) {
            TeamType.SINGLE ->
                queryFactory
                    .selectFrom(payment)
                    .join(singleMeetingTeam)
                    .on(singleMeetingTeam.leader.eq(user))
                    .fetchOne()
            TeamType.TRIPLE ->
                queryFactory
                    .selectFrom(payment)
                    .join(tripleMeetingTeam)
                    .on(tripleMeetingTeam.leader.eq(user))
                    .fetchOne()
        }
    }
}
