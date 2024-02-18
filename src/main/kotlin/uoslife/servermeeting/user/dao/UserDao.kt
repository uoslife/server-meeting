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
    // 매칭되지않은 meetingTeam의 user 중에서 결제한 유저만 조회
    // 쿼리 최적화 필요
    fun selctNotMatchedUser(): List<User> {
        return queryFactory
            .selectFrom(user)
            .leftJoin(match.maleTeam, meetingTeam)
            .fetchJoin()
            .leftJoin(match.femaleTeam, meetingTeam)
            .fetchJoin()
            .where(match.id.isNull)
            .leftJoin(user.team, meetingTeam)
            .fetchJoin()
            .where(user.payment.status.eq(PaymentStatus.COMPLETE_PAYMENT))
            .fetch()
    }
}
