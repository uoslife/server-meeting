package uoslife.servermeeting.meetingteam.entity

import uoslife.servermeeting.meetingteam.entity.enums.Day
import java.io.Serializable
import uoslife.servermeeting.user.entity.enums.GenderType

data class Information(
    val gender: GenderType? = null,
    val questions: Map<Int, Int>? = null,
    val preferredDays: List<Day>? = null,
) : Serializable
