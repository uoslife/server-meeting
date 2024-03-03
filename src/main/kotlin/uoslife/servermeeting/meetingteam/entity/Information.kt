package uoslife.servermeeting.meetingteam.entity

import uoslife.servermeeting.user.entity.enums.GenderType
import java.io.Serializable

data class Information(
    val gender: GenderType? = null,
    val questions: Map<String, Any>? = null,
) : Serializable
