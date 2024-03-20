package uoslife.servermeeting.meetingteam.dto.request

import io.swagger.v3.oas.annotations.media.Schema

class MeetingTeamInformationUpdateRequest(
    @Schema(description = "질문에 대한 답한(5개)") val questions: List<String>
) {
    fun toMap(): Map<Int, String> {
        return mapOf(
            0 to questions[0],
            1 to questions[1],
            2 to questions[2],
            3 to questions[3],
            4 to questions[4]
        )
    }
}
