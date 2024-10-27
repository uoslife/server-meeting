package uoslife.servermeeting.meetingteam.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Size

class MeetingTeamMessageUpdateRequest(
    @Schema(description = "상대에게 보내는 메세지(10자 이상 작성)", example = "안녕하세요 잘부탁드립니다")
    @field:Size(min = 10)
    val message: String
)
