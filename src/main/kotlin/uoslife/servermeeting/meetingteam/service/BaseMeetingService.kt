package uoslife.servermeeting.meetingteam.service

import java.util.UUID
import uoslife.servermeeting.meetingteam.dto.response.MeetingTeamInformationGetResponse
import uoslife.servermeeting.meetingteam.dto.response.MeetingTeamUserListGetResponse
import uoslife.servermeeting.meetingteam.entity.Information

interface BaseMeetingService {
    fun createMeetingTeam(userUUID: UUID, name: String? = null): String?
    fun joinMeetingTeam(
        userUUID: UUID,
        code: String,
        isJoin: Boolean
    ): MeetingTeamUserListGetResponse?
    fun getMeetingTeamUserList(userUUID: UUID, code: String): MeetingTeamUserListGetResponse
    fun updateMeetingTeamInformation(
        userUUID: UUID,
        information: Information,
    )

    fun getMeetingTeamInformation(userUUID: UUID): MeetingTeamInformationGetResponse
    fun deleteMeetingTeam(userUUID: UUID)
}
