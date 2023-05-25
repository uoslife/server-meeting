package uoslife.servermeeting.domain.meeting.application.request

import jakarta.validation.constraints.NotBlank

class MeetingTeamInformationUpdateRequest(
    @NotBlank
    val informationDistance: String,

    @NotBlank
    val informationFilter: String,

    val informationMeetingTime: String,

    @NotBlank
    val preferenceDistance: String,

    val preferenceFilter: String,
)
