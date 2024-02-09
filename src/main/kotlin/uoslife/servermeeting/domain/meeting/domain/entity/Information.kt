package uoslife.servermeeting.domain.meeting.domain.entity

import java.io.Serializable

data class Information(
    val meetingTime: String? = null,
    val meetingPlace: String? = null,
    val gender: String? = null,
    val questions: Map<String, Any>? = null,
) : Serializable
