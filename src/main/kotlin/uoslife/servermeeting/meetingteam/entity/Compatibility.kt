package uoslife.servermeeting.meetingteam.entity

import com.fasterxml.jackson.annotation.JsonAnySetter
import java.io.Serializable

class Compatibility(
    gender: String? = null,
    score: Map<String, Int> = mapOf(),
) : Serializable {

    var score: Map<String, Int> = LinkedHashMap()
        @JsonAnySetter
        set(value) {
            field.plus(value)
        }
}
