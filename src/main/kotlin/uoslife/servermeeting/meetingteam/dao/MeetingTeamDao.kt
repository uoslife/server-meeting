package uoslife.servermeeting.meetingteam.dao

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import uoslife.servermeeting.match.entity.QMatch.match
import uoslife.servermeeting.meetingteam.entity.QSingleMeetingTeam.singleMeetingTeam
import uoslife.servermeeting.user.entity.QUser.user

@Repository
class MeetingTeamDao(
    private val queryFactory: JPAQueryFactory,
) {
    fun findUserIdWithMaleMeetingTeam(): List<Long> {
        return queryFactory
            .select(user.id)
            .from(user)
            .innerJoin(user.singleTeam, singleMeetingTeam)
            .innerJoin(singleMeetingTeam.maleMatch, match)
            .fetch()
    }

    fun findUserIdWithFeMaleMeetingTeam(): List<Long> {
        return queryFactory
            .select(user.id)
            .from(user)
            .innerJoin(user.singleTeam, singleMeetingTeam)
            .innerJoin(singleMeetingTeam.femaleMatch, match)
            .fetch()
    }
}
