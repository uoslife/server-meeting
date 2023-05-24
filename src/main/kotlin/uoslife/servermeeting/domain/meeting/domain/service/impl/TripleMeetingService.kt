package uoslife.servermeeting.domain.meeting.domain.service.impl

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import uoslife.servermeeting.domain.meeting.application.response.MeetingTeamInformationGetResponse
import uoslife.servermeeting.domain.meeting.application.response.MeetingTeamUserListGetResponse
import uoslife.servermeeting.domain.meeting.domain.service.BaseMeetingService
import java.util.*

@Service
@Qualifier("tripleMeetingService")
class TripleMeetingService : BaseMeetingService {
    override fun createMeetingTeam(userUUID: UUID, name: String?): String? {
        TODO("Not yet implemented")
    }

    override fun joinMeetingTeam(userUUID: UUID, code: String) {
        TODO("Not yet implemented")
    }

    override fun getMeetingTeamUserList(userUUID: UUID): MeetingTeamUserListGetResponse {
        TODO("Not yet implemented")
    }

    override fun updateMeetingTeamInformation(
        userUUID: UUID,
        informationDistance: String,
        informationFilter: String,
        informationMeetingTime: String,
        preferenceDistance: String,
        preferenceFilter: String,
    ) {
        TODO("Not yet implemented")
    }

    override fun getMeetingTeamInformation(userUUID: UUID): MeetingTeamInformationGetResponse {
        TODO("Not yet implemented")
    }

    override fun deleteMeetingTeam(userUUID: UUID) {
        TODO("Not yet implemented")
    }
}
