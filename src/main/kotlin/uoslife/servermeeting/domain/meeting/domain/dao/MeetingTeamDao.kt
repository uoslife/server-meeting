package uoslife.servermeeting.domain.meeting.domain.dao

import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.transaction.Transactional
import org.springframework.stereotype.Repository
import uoslife.servermeeting.domain.meeting.domain.entity.MeetingTeam
import uoslife.servermeeting.domain.meeting.domain.entity.QMeetingTeam.meetingTeam
import uoslife.servermeeting.domain.meeting.domain.entity.QUserTeam.userTeam
import uoslife.servermeeting.domain.meeting.domain.entity.UserTeam
import uoslife.servermeeting.domain.meeting.domain.entity.enums.TeamType
import uoslife.servermeeting.domain.user.domain.entity.QUser.user
import uoslife.servermeeting.domain.user.domain.entity.enums.DepartmentNameType
import uoslife.servermeeting.domain.user.domain.entity.enums.GenderType

@Repository
@Transactional
class MeetingTeamDao(
    private val queryFactory: JPAQueryFactory,
) {
    fun findMeetingTeamByGenderAndTeamType(genderType: GenderType, teamType: TeamType): List<MeetingTeam> {
        return queryFactory.selectFrom(meetingTeam)
            .where(
                meetingTeam.information.isNotNull
                    .and(userTeam.type.eq(teamType))
                    .and(user.gender.eq(genderType)),
            )
            .leftJoin(meetingTeam.userTeams, userTeam)
            .leftJoin(userTeam.user, user)
            .fetch()
    }

    fun countMeetingTeamByGenderAndTeamType(genderType: GenderType, teamType: TeamType): Int {
        return queryFactory.selectFrom(meetingTeam)
            .where(
                meetingTeam.information.isNotNull
                    .and(userTeam.type.eq(teamType))
                    .and(user.gender.eq(genderType)),
            )
            .leftJoin(meetingTeam.userTeams, userTeam)
            .leftJoin(userTeam.user, user)
            .fetch().size
    }

    fun getGenderTypeByMeetingTeam(team: MeetingTeam): GenderType? {
        return queryFactory.selectFrom(meetingTeam)
            .where(meetingTeam.eq(team))
            .leftJoin(meetingTeam.userTeams, userTeam)
            .leftJoin(userTeam.user, user)
            .fetchOne()?.userTeams?.first()?.user?.gender
    }

    fun getTeamDepartmentByMeetingTeam(team: MeetingTeam): List<DepartmentNameType> {
        val teamDepartment = mutableListOf<DepartmentNameType?>()
        queryFactory.selectFrom(meetingTeam)
            .where(meetingTeam.eq(team))
            .leftJoin(meetingTeam.userTeams, userTeam)
            .leftJoin(userTeam.user, user)
            .fetchOne()?.userTeams?.forEach { userTeam: UserTeam ->
            teamDepartment.add(userTeam.user?.department)
        }
        return teamDepartment.filterNotNull()
    }

    fun getTeamAgeByMeetingTeam(team: MeetingTeam): List<Int> {
        val teamAge = mutableListOf<Int?>()
        queryFactory.selectFrom(meetingTeam)
            .where(meetingTeam.eq(team))
            .leftJoin(meetingTeam.userTeams, userTeam)
            .leftJoin(userTeam.user, user)
            .fetchOne()?.userTeams?.forEach { userTeam: UserTeam ->
            teamAge.add(userTeam.user?.birthYear)
        }
        return teamAge.filterNotNull()
    }
}
