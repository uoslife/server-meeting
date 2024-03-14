package uoslife.servermeeting.meetingteam.entity

import java.io.Serializable
import uoslife.servermeeting.user.entity.enums.GenderType

data class Information(
    val gender: GenderType? = null,
    val questions: Map<Int, String>? = null,
) : Serializable
