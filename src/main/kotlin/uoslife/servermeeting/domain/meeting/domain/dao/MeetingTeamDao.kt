package uoslife.servermeeting.domain.meeting.domain.dao

import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.transaction.Transactional
import org.springframework.stereotype.Repository
import uoslife.servermeeting.domain.meeting.domain.entity.MeetingTeam
import uoslife.servermeeting.domain.meeting.domain.entity.QMeetingTeam.meetingTeam
import uoslife.servermeeting.domain.meeting.domain.entity.QUserTeam.userTeam
import uoslife.servermeeting.domain.meeting.domain.entity.enums.TeamType
import uoslife.servermeeting.domain.user.domain.entity.QUser.user
import uoslife.servermeeting.domain.user.domain.entity.enums.GenderType

@Repository
@Transactional
class MeetingTeamDao(
    private val queryFactory: JPAQueryFactory,
) {
    fun findMeetingTeamByGenderAndTeamType(genderType: GenderType, teamType: TeamType): List<MeetingTeam> {
        return queryFactory.selectFrom(meetingTeam)
            .where(meetingTeam.information.isNotNull)
            .leftJoin(meetingTeam.userTeams, userTeam)
            .where(userTeam.type.eq(teamType))
            .leftJoin(userTeam.user, user)
            .where(user.gender.eq(genderType))
            .fetch()
    }
    fun countMeetingTeamByGenderAndTeamType(genderType: GenderType, teamType: TeamType): Int {
        return queryFactory.selectFrom(meetingTeam)
            .where(meetingTeam.information.isNotNull)
            .leftJoin(meetingTeam.userTeams, userTeam)
            .where(userTeam.type.eq(teamType))
            .leftJoin(userTeam.user, user)
            .where(user.gender.eq(genderType))
            .fetch().size
    }
    fun getGenderTypeByMeetingTeam(team: MeetingTeam): GenderType? {
        return queryFactory.selectFrom(meetingTeam)
            .where(meetingTeam.eq(team))
            .leftJoin(meetingTeam.userTeams, userTeam)
            .leftJoin(userTeam.user, user)
            .fetchOne()?.userTeams?.first()?.user?.gender
    }
}
