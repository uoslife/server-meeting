package uoslife.servermeeting.match.dto.response

data class MatchParticipationResponse(
    val single: ParticipationStatus,
    val triple: ParticipationStatus
)

data class ParticipationStatus(val isParticipated: Boolean, val matchId: Long?)
