package uoslife.servermeeting.domain.meeting.domain.dao

import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.transaction.Transactional
import org.springframework.stereotype.Repository
import uoslife.servermeeting.domain.meeting.domain.entity.MeetingTeam
import uoslife.servermeeting.domain.meeting.domain.entity.QInformation.information

@Repository
@Transactional
class InformationUpdateDao(private val queryFactory: JPAQueryFactory) {
    fun updateInformationByMeetingTeam(
        meetingTeam: MeetingTeam,
        distanceInfo: String,
        filterInfo: String,
        meetingTime: String,
    ) {
        queryFactory.update(information)
            .set(information.distanceInfo, distanceInfo)
            .set(information.filterInfo, filterInfo)
            .set(information.meetingTime, meetingTime)
            .where(information.meetingTeam.eq(meetingTeam))
            .execute()
    }
}
