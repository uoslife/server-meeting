package uoslife.servermeeting.match.dto.response

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class MeetingParticipationResponse
@JsonCreator
constructor(
    @JsonProperty("single") val single: ParticipationStatus,
    @JsonProperty("triple") val triple: ParticipationStatus
)

data class ParticipationStatus
@JsonCreator
constructor(
    @JsonProperty("participated") val isParticipated: Boolean,
    @JsonProperty("meetingTeamId") val meetingTeamId: Long?
)
