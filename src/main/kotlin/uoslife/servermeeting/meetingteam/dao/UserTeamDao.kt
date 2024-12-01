package uoslife.servermeeting.meetingteam.dao

import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.springframework.stereotype.Repository
import uoslife.servermeeting.match.entity.QMatch.match
import uoslife.servermeeting.meetingteam.entity.MeetingTeam
import uoslife.servermeeting.meetingteam.entity.QMeetingTeam.meetingTeam
import uoslife.servermeeting.meetingteam.entity.QUserTeam.userTeam
import uoslife.servermeeting.meetingteam.entity.UserTeam
import uoslife.servermeeting.meetingteam.entity.enums.TeamType
import uoslife.servermeeting.payment.entity.QPayment.payment
import uoslife.servermeeting.payment.entity.enums.PaymentStatus
import uoslife.servermeeting.user.entity.QUser.user
import uoslife.servermeeting.user.entity.QUserInformation.userInformation
import uoslife.servermeeting.user.entity.User

@Repository
@Transactional
class UserTeamDao(
    private val queryFactory: JPAQueryFactory,
    private val entityManager: EntityManager,
) {
    fun findUserWithMeetingTeam(userId: Long, teamType: TeamType): UserTeam? {
        return queryFactory
            .selectFrom(userTeam)
            .join(userTeam.team, meetingTeam)
            .join(userTeam.user, user)
            .where(user.id.eq(userId).and(meetingTeam.type.eq(teamType)))
            .fetchJoin()
            .fetchOne()
    }
    fun findAllUserTeamWithMeetingTeam(user: User): List<UserTeam>? {
        return queryFactory
            .selectFrom(userTeam)
            .join(userTeam.team, meetingTeam)
            .fetchJoin() // 관계 명시 및 fetchJoin 적용
            .where(userTeam.user.eq(user))
            .fetch()
    }

    fun saveUserTeam(meetingTeam: MeetingTeam, user: User, isLeader: Boolean) {
        entityManager.persist(
            UserTeam(
                team = meetingTeam,
                user = user,
                isLeader = isLeader,
            ),
        )
    }

    fun findByUserWithMeetingTeam(user: User, teamType: TeamType): UserTeam? {
        return queryFactory
            .selectFrom(userTeam)
            .join(userTeam.team, meetingTeam)
            .fetchJoin()
            .where(userTeam.user.eq(user).and(meetingTeam.type.eq(teamType)))
            .fetchOne()
    }

    fun findByTeam(meetingTeam: MeetingTeam): List<UserTeam> {
        return queryFactory
            .selectFrom(userTeam)
            .join(userTeam.user)
            .fetchJoin()
            .where(userTeam.team.eq(meetingTeam))
            .fetch()
    }

    fun findByTeamAndisLeader(meetingTeam: MeetingTeam, isLeader: Boolean): UserTeam? {
        return queryFactory
            .selectFrom(userTeam)
            .join(userTeam.user)
            .fetchJoin()
            .where(userTeam.team.eq(meetingTeam), userTeam.isLeader.eq(isLeader))
            .fetchOne()
    }

    fun countByTeam(meetingTeam: MeetingTeam): Int {
        return queryFactory.selectFrom(userTeam).where(userTeam.team.eq(meetingTeam)).fetch().size
    }

    fun deleteAll() {
        queryFactory.delete(userTeam).execute()
    }

    fun findAllUserTeamWithUserInfoFromMeetingTeam(meetingTeam: MeetingTeam): List<UserTeam> {
        return queryFactory
            .select(userTeam) // fetch join 주체인 userTeam 명시
            .from(userTeam)
            .join(userTeam.user, user)
            .fetchJoin() // user 엔티티 fetch join
            .join(user.userInformation, userInformation)
            .fetchJoin() // userInformation fetch join
            .where(userTeam.team.eq(meetingTeam))
            .fetch()
    }

    fun findAllByUserIdWithPaymentStatus(userId: Long): List<UserTeam> {
        val hasNonSuccessPayment =
            JPAExpressions.selectOne()
                .from(payment)
                .where(
                    payment.meetingTeam.eq(meetingTeam),
                    payment.status.ne(PaymentStatus.SUCCESS)
                )
                .exists()

        return queryFactory
            .selectFrom(userTeam)
            .join(userTeam.team, meetingTeam)
            .fetchJoin()
            .where(userTeam.user.id.eq(userId), hasNonSuccessPayment.not())
            .fetch()
    }

    fun findUserWithMeetingTeamByMatchId(userId: Long, matchId: Long): UserTeam? {
        return queryFactory
            .selectFrom(userTeam)
            .join(userTeam.team, meetingTeam)
            .fetchJoin()
            .leftJoin(match)
            .on(meetingTeam.eq(match.maleTeam).or(meetingTeam.eq(match.femaleTeam)))
            .where(userTeam.user.id.eq(userId), match.id.eq(matchId))
            .fetchOne()
    }
}
