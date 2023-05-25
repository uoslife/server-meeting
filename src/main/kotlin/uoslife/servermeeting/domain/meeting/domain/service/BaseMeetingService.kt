package uoslife.servermeeting.domain.meeting.domain.service

import uoslife.servermeeting.domain.meeting.application.response.MeetingTeamInformationGetResponse
import uoslife.servermeeting.domain.meeting.application.response.MeetingTeamUserListGetResponse
import java.util.*

interface BaseMeetingService {
    fun createMeetingTeam(userUUID: UUID, name: String? = null): String?
    fun joinMeetingTeam(userUUID: UUID, code: String)
    fun getMeetingTeamUserList(userUUID: UUID, code: String): MeetingTeamUserListGetResponse
    fun updateMeetingTeamInformation(
        userUUID: UUID,
        informationDistance: String,
        informationFilter: String,
        informationMeetingTime: String,
        preferenceDistance: String,
        preferenceFilter: String,
    )

    fun getMeetingTeamInformation(userUUID: UUID): MeetingTeamInformationGetResponse
    fun deleteMeetingTeam(userUUID: UUID)
}
