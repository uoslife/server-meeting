package uoslife.servermeeting.meetingteam.entity

import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonSetter
import java.io.Serializable
import uoslife.servermeeting.user.entity.enums.GenderType

class Compatibility(
    gender: GenderType? = null,
    matchSequence: Int = 0,
    score: MutableList<Long> = mutableListOf(),
) : Serializable {
    var matchSequence: Int = 0
    @JsonSetter
    set(value) {
        field = value
    }

    var score: MutableList<Long> = mutableListOf()
        @JsonAnySetter
        set(value) {
            score.addAll(value)
        }
}
