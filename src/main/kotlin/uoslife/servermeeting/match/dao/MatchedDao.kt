package uoslife.servermeeting.match.dao

import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.transaction.Transactional
import org.springframework.stereotype.Repository

// import uoslife.servermeeting.meetingteam.entity.QMeetingTeam.meetingTeam

@Repository
@Transactional
class MatchedDao(private val queryFactory: JPAQueryFactory) {
    //    fun findMatchByMaleTeamWithFemaleTeam(maleTeam: MeetingTeam): Match? {
    //        return queryFactory
    //            .selectFrom(match)
    //            .join(match.femaleTeam, meetingTeam)
    //            .fetchJoin()
    //            .join(meetingTeam.leader, user)
    //            .fetchJoin()
    //            .where(match.maleTeam.eq(maleTeam))
    //            .fetchOne()
    //    }
    //
    //    fun findMatchByFeMaleTeamWithMaleTeam(femaleTeam: MeetingTeam): Match? {
    //        return queryFactory
    //            .selectFrom(match)
    //            .join(match.maleTeam, meetingTeam)
    //            .fetchJoin()
    //            .join(meetingTeam.leader, user)
    //            .fetchJoin()
    //            .where(match.femaleTeam.eq(femaleTeam))
    //            .fetchOne()
    //    }
}
