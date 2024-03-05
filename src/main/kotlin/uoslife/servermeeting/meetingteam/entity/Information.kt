package uoslife.servermeeting.meetingteam.entity

import java.io.Serializable
import uoslife.servermeeting.user.entity.enums.GenderType

class Information(
    gender: GenderType? = null,
    questions: Map<String, Any>? = null,
) : Serializable
