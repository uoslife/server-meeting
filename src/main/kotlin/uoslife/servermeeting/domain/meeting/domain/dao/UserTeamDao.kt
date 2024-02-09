package uoslife.servermeeting.domain.meeting.domain.dao

import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.springframework.stereotype.Repository
import uoslife.servermeeting.domain.meeting.domain.entity.MeetingTeam
import uoslife.servermeeting.domain.meeting.domain.entity.QUserTeam.userTeam
import uoslife.servermeeting.domain.meeting.domain.entity.UserTeam
import uoslife.servermeeting.domain.meeting.domain.entity.enums.TeamType
import uoslife.servermeeting.domain.user.domain.entity.User

@Repository
@Transactional
class UserTeamDao(
    private val queryFactory: JPAQueryFactory,
    private val entityManager: EntityManager,
) {
    fun saveUserTeam(userTeam: UserTeam) {
        entityManager.persist(userTeam)
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
            .where(userTeam.type.eq(teamType))
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
}
