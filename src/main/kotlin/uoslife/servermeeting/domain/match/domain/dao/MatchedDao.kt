package uoslife.servermeeting.domain.match.domain.dao

import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.transaction.Transactional
import org.springframework.stereotype.Repository
import uoslife.servermeeting.domain.match.domain.entity.QMatch.match
import uoslife.servermeeting.domain.meeting.domain.entity.MeetingTeam
import uoslife.servermeeting.domain.user.domain.entity.enums.GenderType
import uoslife.servermeeting.domain.user.domain.entity.enums.GenderType.FEMALE
import uoslife.servermeeting.domain.user.domain.entity.enums.GenderType.MALE

@Repository
@Transactional
class MatchedDao(private val queryFactory: JPAQueryFactory) {

    fun findMatchedTeamByTeamAndGender(
        meetingTeam: MeetingTeam,
        gender: GenderType,
    ): MeetingTeam? {
        return when (gender) {
            MALE ->
                queryFactory
                    .selectFrom(match)
                    .where(match.maleTeam.eq(meetingTeam))
                    .fetchOne()
                    ?.femaleTeam
            FEMALE ->
                queryFactory
                    .selectFrom(match)
                    .where(match.femaleTeam.eq(meetingTeam))
                    .fetchOne()
                    ?.maleTeam
        }
    }
}
