package uoslife.servermeeting.meetingteam.dto.request

import jakarta.validation.constraints.Size

class MeetingTeamMessageUpdateRequest(
    @field:Size(min = 10)
    val message: String?
)
