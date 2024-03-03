package uoslife.servermeeting.meetingteam.service

import java.util.UUID
import uoslife.servermeeting.meetingteam.dto.request.MeetingTeamInformationUpdateRequest
import uoslife.servermeeting.meetingteam.dto.request.MeetingTeamPreferenceUpdateRequest
import uoslife.servermeeting.meetingteam.dto.response.MeetingTeamInformationGetResponse
import uoslife.servermeeting.meetingteam.dto.response.MeetingTeamUserListGetResponse
import uoslife.servermeeting.meetingteam.entity.enums.TeamType

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
    fun createMeetingTeam(userUUID: UUID, name: String? = null, teamType: TeamType): String?
    fun joinMeetingTeam(
        userUUID: UUID,
        code: String,
        isJoin: Boolean
    ): MeetingTeamUserListGetResponse?
    fun getMeetingTeamUserList(userUUID: UUID, code: String): MeetingTeamUserListGetResponse
    fun updateMeetingTeamInformation(
        userUUID: UUID,
        meetingTeamInformationUpdateRequest: MeetingTeamInformationUpdateRequest
    )
    fun updateMeetingTeamPreference(
        userUUID: UUID,
        meetingTeamPreferenceUpdateRequest: MeetingTeamPreferenceUpdateRequest
    )
    fun getMeetingTeamInformation(userUUID: UUID): MeetingTeamInformationGetResponse
    fun getMeetingTeamInformationByKakaoId(kakaoId: String): MeetingTeamInformationGetResponse
    fun deleteMeetingTeam(userUUID: UUID)
}
