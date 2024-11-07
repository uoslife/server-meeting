package uoslife.servermeeting.meetingteam.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import uoslife.servermeeting.meetingteam.entity.Preference
import uoslife.servermeeting.meetingteam.entity.enums.TeamMood
import uoslife.servermeeting.user.entity.enums.ReligionType
import uoslife.servermeeting.user.entity.enums.SmokingType
import uoslife.servermeeting.user.entity.enums.StudentType

class MeetingTeamPreferenceUpdateRequest(
    @Schema(description = "최소 나이", example = "20", nullable = false) @field:NotNull val ageMin: Int,
    @Schema(description = "최대 나이", example = "30", nullable = false) @field:NotNull val ageMax: Int,
    @Schema(description = "최소 키", example = "130") val heightMin: Int?,
    @Schema(description = "최대 키", example = "210") val heightMax: Int?,
    @Schema(description = "신분", example = "[\"UNDERGRADUATE\", \"POSTGRADUATE\"]")
    val studentType: List<StudentType>?,
    @Schema(description = "종교", example = "[\"CHRISTIAN\", \"CATHOLIC\"]")
    val religion: List<ReligionType>?,
    @Schema(description = "흡연 여부", example = "[\"TRUE\"]") val smoking: List<SmokingType>?,
    @Schema(description = "최소 음주 횟수") val drinkingMin: Int?,
    @Schema(description = "최대 음주 횟수") val drinkingMax: Int?,
    @Schema(description = "MBTI", example = "EINTFJP") val mbti: String?,
    @Schema(description = "미팅 분위기", example = "ACTIVE") val mood: TeamMood?,
) {
    fun toSinglePreference(validMBTI: String?): Preference {
        return Preference(
            ageMin = ageMin,
            ageMax = ageMax,
            heightMin = heightMin,
            heightMax = heightMax,
            studentType = studentType,
            religion = religion,
            smoking = smoking,
            drinkingMin = drinkingMin,
            drinkingMax = drinkingMax,
            mbti = validMBTI,
        )
    }

    fun toTriplePreference(): Preference {
        return Preference(
            ageMin = ageMin,
            ageMax = ageMax,
            mood = mood,
        )
    }
}
