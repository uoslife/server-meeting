package uoslife.servermeeting.user.dao

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import uoslife.servermeeting.meetingteam.entity.MeetingTeam
import uoslife.servermeeting.meetingteam.entity.QMeetingTeam.meetingTeam
import uoslife.servermeeting.meetingteam.entity.enums.PaymentStatus
import uoslife.servermeeting.meetingteam.entity.enums.TeamType
import uoslife.servermeeting.user.entity.QUser.user
import uoslife.servermeeting.user.entity.User
import java.util.*

@Repository
class UserDao(
    private val queryFactory: JPAQueryFactory,
) {
    fun findUserWithMeetingTeam(userId: UUID, teamType: TeamType): User? {
        return queryFactory
            .selectFrom(user)
            .join(user.team, meetingTeam)
            .fetchJoin()
            .where(user.id.eq(userId))
            .where(meetingTeam.type.eq(teamType))
            .fetchOne()
    }
    fun findNotMatchedUserInMeetingTeam(meetingTeamList: List<MeetingTeam>): List<User> {
        return queryFactory
            .selectFrom(user)
            .leftJoin(user.team, meetingTeam)
            .fetchJoin()
            .where(
                user.payment.status.eq(PaymentStatus.SUCCESS).and(meetingTeam.`in`(meetingTeamList))
            )
            .fetch()
    }
}
