package uoslife.servermeeting.match.dao

import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.transaction.Transactional
import org.springframework.stereotype.Repository
import uoslife.servermeeting.match.dto.response.MeetingParticipationResponse
import uoslife.servermeeting.match.entity.Match
import uoslife.servermeeting.match.entity.QMatch.match
import uoslife.servermeeting.meetingteam.entity.MeetingTeam
import uoslife.servermeeting.meetingteam.entity.QMeetingTeam.meetingTeam
import uoslife.servermeeting.meetingteam.entity.QUserTeam.userTeam
import uoslife.servermeeting.meetingteam.entity.enums.TeamType
import uoslife.servermeeting.payment.entity.enums.PaymentStatus

@Repository
@Transactional
class MatchedDao(private val queryFactory: JPAQueryFactory) {
    fun findMatchByMaleTeamWithFemaleTeam(maleTeam: MeetingTeam): Match? {
        return queryFactory
            .selectFrom(match)
            .join(match.femaleTeam, meetingTeam)
            .join(meetingTeam.userTeams, userTeam)
            .where(match.maleTeam.eq(maleTeam).and(userTeam.isLeader.eq(true)))
            .fetchOne()
    }

    fun findMatchByFeMaleTeamWithMaleTeam(femaleTeam: MeetingTeam): Match? {
        return queryFactory
            .selectFrom(match)
            .join(match.maleTeam, meetingTeam)
            .join(meetingTeam.userTeams, userTeam)
            .where(match.femaleTeam.eq(femaleTeam).and(userTeam.isLeader.eq(true)))
            .fetchOne()
    }

    fun findUserParticipation(userId: Long, season: Int): MeetingParticipationResponse {
        val teams =
            queryFactory
                .selectFrom(userTeam)
                .join(userTeam.team)
                .fetchJoin() // team을 미리 로딩
                .leftJoin(userTeam.team.payments)
                .fetchJoin() // payments도 미리 로딩
                .where(userTeam.user.id.eq(userId), userTeam.team.season.eq(season))
                .fetch()

        val participations =
            teams
                .groupBy { it.team.type }
                .mapValues { (_, userTeams) ->
                    userTeams.all { userTeam ->
                        val payments = userTeam.team.payments
                        payments?.isNotEmpty() == true &&
                            payments.all { it.status == PaymentStatus.SUCCESS }
                    }
                }

        return MeetingParticipationResponse(
            single = participations[TeamType.SINGLE] ?: false,
            triple = participations[TeamType.TRIPLE] ?: false,
        )
    }
}
