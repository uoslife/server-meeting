package uoslife.servermeeting.domain.meeting.domain.entity

import com.fasterxml.jackson.annotation.JsonAnySetter
import java.io.Serializable

class Information(
    meetingTime: String? = null,
    meetingPlace: String? = null,
    gender: String? = null,
    questions: Map<String, Any>? = null,
) : Serializable {

    var questions: Map<String, Any> = LinkedHashMap()
        @JsonAnySetter
        set(value) {
            field.plus(value)
        }
}
