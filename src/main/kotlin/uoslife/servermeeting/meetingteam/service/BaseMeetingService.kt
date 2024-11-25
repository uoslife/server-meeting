package uoslife.servermeeting.meetingteam.service

import uoslife.servermeeting.meetingteam.dto.request.MeetingTeamInfoUpdateRequest
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
    ): MeetingTeamUserListGetResponse?
    fun getMeetingTeamUserList(userId: Long, code: String): MeetingTeamUserListGetResponse
    fun updateMeetingTeamInfo(
        userId: Long,
        meetingTeamInfoUpdateRequest: MeetingTeamInfoUpdateRequest
    )
    fun getMeetingTeamInformation(userId: Long): MeetingTeamInformationGetResponse
    fun deleteMeetingTeam(userId: Long)
}
