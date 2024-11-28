package uoslife.servermeeting.meetingteam.dao

import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.springframework.stereotype.Repository
import uoslife.servermeeting.meetingteam.entity.MeetingTeam
import uoslife.servermeeting.meetingteam.entity.QMeetingTeam.meetingTeam
import uoslife.servermeeting.meetingteam.entity.QUserTeam.userTeam
import uoslife.servermeeting.meetingteam.entity.UserTeam
import uoslife.servermeeting.meetingteam.entity.enums.TeamType
import uoslife.servermeeting.user.entity.QUser.user
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

    fun findByUser(user: User): UserTeam? {
        return queryFactory.selectFrom(userTeam).where(userTeam.user.eq(user)).fetchOne()
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
}
