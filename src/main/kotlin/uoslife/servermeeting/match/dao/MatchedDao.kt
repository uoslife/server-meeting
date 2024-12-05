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
import uoslife.servermeeting.payment.entity.QPayment
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
        val result =
            queryFactory
                .select(userTeam.team.id, userTeam.team.type)
                .from(userTeam)
                .join(userTeam.team)
                .join(QPayment.payment)
                .on(QPayment.payment.meetingTeam.eq(userTeam.team))
                .where(
                    userTeam.user.id.eq(userId),
                    userTeam.team.season.eq(season),
                    QPayment.payment.status.eq(PaymentStatus.SUCCESS)
                )
                .fetch()

        val participations = result.groupBy { it.get(userTeam.team.type) }

        return MeetingParticipationResponse(
            single = participations[TeamType.SINGLE]?.isNotEmpty() ?: false,
            triple = participations[TeamType.TRIPLE]?.isNotEmpty() ?: false,
        )
    }
}
