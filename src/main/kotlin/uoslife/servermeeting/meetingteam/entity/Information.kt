package uoslife.servermeeting.meetingteam.entity

import java.io.Serializable
import uoslife.servermeeting.match.dto.response.MatchedInformation
import uoslife.servermeeting.user.entity.enums.GenderType

data class Information(
    val gender: GenderType? = null,
    val questions: Map<Int, Int>? = null,
) : Serializable {
    fun toMatchedInformation(): MatchedInformation {
        return MatchedInformation(gender = gender, questions = questions?.values?.toList())
    }
}
