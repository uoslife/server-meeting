package uoslife.servermeeting.match.dto.response

data class MeetingParticipationResponse(
    val single: ParticipationStatus,
    val triple: ParticipationStatus
)

data class ParticipationStatus(val isParticipated: Boolean, val meetingTeamId: Long?)
