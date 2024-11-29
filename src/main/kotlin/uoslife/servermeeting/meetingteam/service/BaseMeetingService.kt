package uoslife.servermeeting.meetingteam.service

import uoslife.servermeeting.meetingteam.dto.request.MeetingTeamInfoUpdateRequest
import uoslife.servermeeting.meetingteam.dto.response.MeetingTeamCodeResponse
import uoslife.servermeeting.meetingteam.dto.response.MeetingTeamInformationGetResponse
import uoslife.servermeeting.meetingteam.dto.response.MeetingTeamLeaderNameResponse

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
    ): MeetingTeamCodeResponse
    fun joinMeetingTeam(
        userId: Long,
        code: String,
    ): MeetingTeamLeaderNameResponse
    fun getMeetingTeamUserList(code: String): MeetingTeamLeaderNameResponse
    fun updateMeetingTeamInfo(
        userId: Long,
        meetingTeamInfoUpdateRequest: MeetingTeamInfoUpdateRequest
    )
    fun getMeetingTeamInformation(userId: Long): MeetingTeamInformationGetResponse
    fun deleteMeetingTeam(userId: Long)
}
