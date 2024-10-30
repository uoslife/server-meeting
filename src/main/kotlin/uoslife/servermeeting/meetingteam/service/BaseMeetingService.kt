package uoslife.servermeeting.meetingteam.service

import uoslife.servermeeting.meetingteam.dto.request.MeetingTeamInformationUpdateRequest
import uoslife.servermeeting.meetingteam.dto.request.MeetingTeamMessageUpdateRequest
import uoslife.servermeeting.meetingteam.dto.request.MeetingTeamPreferenceUpdateRequest
import uoslife.servermeeting.meetingteam.dto.response.MeetingTeamCodeResponse
import uoslife.servermeeting.meetingteam.dto.response.MeetingTeamInformationGetResponse
import uoslife.servermeeting.meetingteam.dto.response.MeetingTeamUserListGetResponse

interface BaseMeetingService {
    /**
     * Create meeting team
     *
     * @param userUUID
     * @param name
     * @param isLeader
     * @param teamType
     * @return
     */
    fun createMeetingTeam(
        userId: Long,
        name: String? = null,
    ): MeetingTeamCodeResponse
    fun joinMeetingTeam(
        userId: Long,
        code: String,
        isJoin: Boolean
    ): MeetingTeamUserListGetResponse?
    fun getMeetingTeamUserList(userId: Long, code: String): MeetingTeamUserListGetResponse
    fun updateMeetingTeamInformation(
        userId: Long,
        meetingTeamInformationUpdateRequest: MeetingTeamInformationUpdateRequest
    )
    fun updateMeetingTeamPreference(
        userId: Long,
        meetingTeamPreferenceUpdateRequest: MeetingTeamPreferenceUpdateRequest
    )

    fun updateMeetingTeamMessage(
        userId: Long,
        meetingTeamMessageUpdateRequest: MeetingTeamMessageUpdateRequest
    )
    fun getMeetingTeamInformation(userId: Long): MeetingTeamInformationGetResponse
    fun deleteMeetingTeam(userId: Long)
}
