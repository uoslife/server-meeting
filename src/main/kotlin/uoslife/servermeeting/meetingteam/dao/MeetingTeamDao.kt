package uoslife.servermeeting.meetingteam.dao

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import uoslife.servermeeting.match.entity.QMatch.match
import uoslife.servermeeting.meetingteam.entity.MeetingTeam
import uoslife.servermeeting.meetingteam.entity.QMeetingTeam.meetingTeam
import uoslife.servermeeting.meetingteam.entity.enums.PaymentStatus
import uoslife.servermeeting.meetingteam.entity.enums.TeamType
import uoslife.servermeeting.user.entity.QUser.user
import uoslife.servermeeting.user.entity.enums.GenderType

@Repository
class MeetingTeamDao(
    private val queryFactory: JPAQueryFactory,
) {
    fun findNotMatchedMaleMeetingTeam(): List<MeetingTeam> {
        return queryFactory
            .selectFrom(meetingTeam)
            .leftJoin(meetingTeam.maleMatch, match)
            .fetchJoin()
            .where(match.id.isNull)
            .fetch()
    }

    fun findNotMatchedFeMaleMeetingTeam(): List<MeetingTeam> {
        return queryFactory
            .selectFrom(meetingTeam)
            .leftJoin(meetingTeam.femaleMatch, match)
            .fetchJoin()
            .where(match.id.isNull)
            .fetch()
    }

    // 유저 전부 돈을 지불한 meetingTeam 조회 (male, female 구분) -> 매칭시킬 meetingTeam
    // TODO: QUERY 다시 짜기
    fun findMatchingMeetingTeamByTeamTypeAndGenderType(
        type: TeamType,
        genderType: GenderType
    ): MutableList<MeetingTeam> {
        return queryFactory
            .selectFrom(meetingTeam)
            .leftJoin(meetingTeam.users, user)
            .fetchJoin()
            .where(
                user.payment.status.eq(PaymentStatus.SUCCESS)
                    .and(meetingTeam.type.eq(type))
                    .and(meetingTeam.leader.gender.eq(genderType))
            )
            .distinct()
            .fetch()
    }
}
