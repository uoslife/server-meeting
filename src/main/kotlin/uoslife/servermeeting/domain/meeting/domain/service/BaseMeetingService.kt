package uoslife.servermeeting.domain.meeting.domain.service

import java.util.*
import uoslife.servermeeting.domain.meeting.application.response.MeetingTeamInformationGetResponse
import uoslife.servermeeting.domain.meeting.application.response.MeetingTeamUserListGetResponse
import uoslife.servermeeting.domain.meeting.domain.entity.Information

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
