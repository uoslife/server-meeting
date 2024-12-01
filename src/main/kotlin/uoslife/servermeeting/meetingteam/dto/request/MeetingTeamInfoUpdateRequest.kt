package uoslife.servermeeting.meetingteam.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import uoslife.servermeeting.meetingteam.entity.MeetingTeam
import uoslife.servermeeting.meetingteam.entity.Preference
import uoslife.servermeeting.meetingteam.entity.enums.TeamMood
import uoslife.servermeeting.meetingteam.entity.enums.TeamType
import uoslife.servermeeting.meetingteam.entity.enums.Weight
import uoslife.servermeeting.user.entity.enums.*

class MeetingTeamInfoUpdateRequest(
    @Schema(description = "1:1 상대에게 보내는 데이트 코스", example = "같이 밥먹고 눈사람 만들기")
    @field:Size(min = 1)
    val course: String?,
    @Schema(description = "3:3 팀 이름 (2~8자)", example = "우당탕탕 시립대")
    @field:Size(min = 2, max = 8)
    val name: String?,
    @Schema(description = "최소 나이", example = "20", nullable = false) @field:NotNull val ageMin: Int,
    @Schema(description = "최대 나이", example = "30", nullable = false) @field:NotNull val ageMax: Int,
    @Schema(description = "최소 키", example = "150") val heightMin: Int?,
    @Schema(description = "최대 키", example = "190") val heightMax: Int?,
    @Schema(description = "외모1 (상관없음 시, 모두)", example = "[\"ARAB\",\"NORMAL\",\"TOFU\"]")
    val appearanceType: MutableList<AppearanceType>?,
    @Schema(description = "외모2 (상관없음 시, 모두)", example = "[\"SINGLE\", \"INNER\",\"DOUBLE\"]")
    val eyelidType: MutableList<EyelidType>?,
    @Schema(
        description = "흡연 여부 (상관없음 시, 모두)",
        example = "[\"FALSE\", \"E_CIGARETTE\", \"CIGARETTE\"]"
    )
    val smoking: MutableList<SmokingType>?,
    @Schema(description = "MBTI", example = "EINTFJP") val mbti: String?,
    @Schema(description = "미팅 분위기", example = "ACTIVE") val mood: TeamMood?,
    @Schema(description = "선호 가중치", example = "HEIGHT") val weight: Weight?,
    @Schema(description = "피하고 싶은 학번", example = "19") val avoidanceNumber: Int?,
    @Schema(description = "피하고 싶은 학과", example = "통계학과") val avoidanceDepartment: String?,
) {
    companion object {
        const val MINIMUM_AGE = 20
        const val MAXIMAL_AGE = 30
    }
    fun toSinglePreference(validMBTI: String?, meetingTeam: MeetingTeam): Preference {
        return Preference(
            ageMin =
                if (ageMin < MINIMUM_AGE) { //20살 미만
                    MINIMUM_AGE
                } else if (ageMin > MAXIMAL_AGE) { //30살 초과
                    MAXIMAL_AGE
                } else ageMin,
            ageMax =
                if (ageMax > MAXIMAL_AGE) {
                    MAXIMAL_AGE
                } else if (ageMax < MINIMUM_AGE) {
                    MINIMUM_AGE
                } else ageMax,
            heightMin = heightMin,
            heightMax = heightMax,
            appearanceType = appearanceType,
            eyelidType = eyelidType,
            smoking = smoking,
            mbti = validMBTI,
            weight = weight,
            meetingTeam = meetingTeam,
            avoidanceDepartment = avoidanceDepartment,
            avoidanceNumber = avoidanceNumber,
        )
    }

    fun toTriplePreference(meetingTeam: MeetingTeam): Preference {
        return Preference(
            ageMin = ageMin,
            ageMax = ageMax,
            mood = mood,
            meetingTeam = meetingTeam
        )
    }
}
