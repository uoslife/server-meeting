package uoslife.servermeeting.meetingteam.dto.request

import jakarta.validation.constraints.NotBlank

class MeetingTeamInformationUpdateRequest(
    @NotBlank val question1: Any,
    @NotBlank val question2: Int,
    @NotBlank val question3: Int,
    @NotBlank val question4: Int,
    @NotBlank val question5: Int,
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "question1" to question1,
            "question2" to question2,
            "question3" to question3,
            "question4" to question4,
            "question5" to question5
        )
    }
}
