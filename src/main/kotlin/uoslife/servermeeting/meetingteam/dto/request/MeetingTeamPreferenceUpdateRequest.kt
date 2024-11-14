package uoslife.servermeeting.meetingteam.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import uoslife.servermeeting.meetingteam.entity.MeetingTeam
import uoslife.servermeeting.meetingteam.entity.Preference
import uoslife.servermeeting.meetingteam.entity.enums.TeamMood
import uoslife.servermeeting.meetingteam.entity.enums.Weight
import uoslife.servermeeting.user.entity.enums.*

class MeetingTeamPreferenceUpdateRequest(
    @Schema(description = "최소 나이", example = "20", nullable = false) @field:NotNull val ageMin: Int,
    @Schema(description = "최대 나이", example = "30", nullable = false) @field:NotNull val ageMax: Int,
    @Schema(description = "최소 키", example = "130") val heightMin: Int?,
    @Schema(description = "최대 키", example = "210") val heightMax: Int?,
    @Schema(description = "외모1", example = "[\"ARAB\", \"TOFU\"]")
    val appearanceType: List<AppearanceType>?,
    @Schema(description = "외모2", example = "[\"SINGLE\", \"INNER\"]")
    val eyelidType: List<EyelidType>?,
    @Schema(description = "흡연 여부", example = "[\"FALSE\", \"E_CIGARETTE\"]")
    val smoking: List<SmokingType>?,
    @Schema(description = "MBTI", example = "EINTFJP") val mbti: String?,
    @Schema(description = "미팅 분위기", example = "ACTIVE") val mood: TeamMood?,
    @Schema(description = "선호 가중치", example = "HEIGHT") val weight: Weight?,
    @Schema(description = "피하고 싶은 학번", example = "19") val avoidanceNumber: Int?,
    @Schema(description = "피하고 싶은 학과", example = "통계학과") val avoidanceDepartment: String?,
) {
    fun toSinglePreference(validMBTI: String?, meetingTeam: MeetingTeam): Preference {
        return Preference(
            ageMin = ageMin,
            ageMax = ageMax,
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
        return Preference(ageMin = ageMin, ageMax = ageMax, mood = mood, meetingTeam = meetingTeam)
    }
}
