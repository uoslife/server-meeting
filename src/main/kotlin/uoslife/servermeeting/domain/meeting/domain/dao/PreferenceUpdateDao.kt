package uoslife.servermeeting.domain.meeting.domain.dao

import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import uoslife.servermeeting.domain.meeting.domain.entity.MeetingTeam
import uoslife.servermeeting.domain.meeting.domain.entity.QPreference.preference

@Service
@Transactional
class PreferenceUpdateDao(
    private val queryFactory: JPAQueryFactory,
) {
    fun updatePreferenceByMeetingTeam(
        meetingTeam: MeetingTeam,
        preferenceFilter: String,
        preferenceDistance: String,
    ) {
        queryFactory.update(preference)
            .set(preference.filterCondition, preferenceFilter)
            .set(preference.distanceCondition, preferenceDistance)
            .where(preference.meetingTeam.eq(meetingTeam))
            .execute()
    }
}
