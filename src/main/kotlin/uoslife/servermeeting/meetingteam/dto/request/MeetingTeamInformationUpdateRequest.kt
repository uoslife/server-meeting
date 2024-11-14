package uoslife.servermeeting.meetingteam.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import uoslife.servermeeting.meetingteam.entity.Information
import uoslife.servermeeting.meetingteam.entity.MeetingTeam
import uoslife.servermeeting.user.entity.enums.GenderType

class MeetingTeamInformationUpdateRequest(
    @Schema(description = "질문에 대한 답안(5개)", nullable = false) val questions: List<Int>
) {
    fun toInformation(gender: GenderType, meetingTeam: MeetingTeam): Information {
        return Information(
            meetingTeam = meetingTeam,
            gender = gender,
        )
    }
}
