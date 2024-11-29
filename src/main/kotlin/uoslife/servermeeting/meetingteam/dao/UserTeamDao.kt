package uoslife.servermeeting.meetingteam.dao

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
            .join(meetingTeam)
            .join(user)
            .where(userTeam.user.id.eq(userId).and(userTeam.team.type.eq(teamType)))
            .fetchJoin()
            .fetchOne()
    }

    fun findUserTeamWithMeetingTeam(user: User): List<UserTeam>? {
        return queryFactory
            .selectFrom(userTeam)
            .join(meetingTeam)
            .where(userTeam.user.eq(user))
            .fetchJoin()
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
            .join(userTeam.team)
            .fetchJoin()
            .where(userTeam.user.eq(user))
            .where(userTeam.team.type.eq(teamType))
            .fetchOne()
    }

    fun findByUserAndTeam(user: User, meetingTeam: MeetingTeam): UserTeam? {
        return queryFactory
            .selectFrom(userTeam)
            .where(userTeam.user.eq(user), userTeam.team.eq(meetingTeam))
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

    fun findAllByUserId(userId: Long): List<UserTeam> {
        return queryFactory
            .selectFrom(userTeam)
            .join(userTeam.team, meetingTeam)
            .fetchJoin()
            .where(userTeam.user.id.eq(userId))
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
