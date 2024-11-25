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
    @Schema(description = "1:1 상대에게 보내는 메세지(10자 이상 작성)", example = "안녕하세요 잘부탁드립니다")
    @field:Size(min = 10)
    val message: String?,
    @Schema(description = "3:3 팀 이름 (2~8자)", example = "우당탕탕 시립대")
    @field:Size(min = 2, max = 8)
    val name: String?,
    @Schema(description = "최소 나이", example = "20", nullable = false) @field:NotNull val ageMin: Int,
    @Schema(description = "최대 나이", example = "30", nullable = false) @field:NotNull val ageMax: Int,
    @Schema(description = "최소 키", example = "150") val heightMin: Int?,
    @Schema(description = "최대 키", example = "190") val heightMax: Int?,
    @Schema(description = "외모1", example = "[\"ARAB\", \"TOFU\"]")
    val appearanceType: MutableList<AppearanceType>?,
    @Schema(description = "외모2", example = "[\"SINGLE\", \"INNER\"]")
    val eyelidType: MutableList<EyelidType>?,
    @Schema(description = "흡연 여부", example = "[\"FALSE\", \"E_CIGARETTE\"]")
    val smoking: MutableList<SmokingType>?,
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

    fun updatePreference(preference: Preference, validMBTI: String?, teamType: TeamType) {
        when (teamType) {
            TeamType.SINGLE -> {
                preference.ageMin = ageMin ?: preference.ageMin
                preference.ageMax = ageMax ?: preference.ageMax
                preference.heightMin = heightMin ?: preference.heightMin
                preference.heightMax = heightMax ?: preference.heightMax
                preference.appearanceType = appearanceType ?: preference.appearanceType
                preference.eyelidType = eyelidType ?: preference.eyelidType
                preference.smoking = smoking ?: preference.smoking
                preference.mbti = validMBTI ?: preference.mbti
                preference.weight = weight ?: preference.weight
                preference.avoidanceDepartment =
                    avoidanceDepartment ?: preference.avoidanceDepartment
                preference.avoidanceNumber = avoidanceNumber ?: preference.avoidanceNumber
            }
            TeamType.TRIPLE -> {
                preference.ageMin = ageMin ?: preference.ageMin
                preference.ageMax = ageMax ?: preference.ageMax
                preference.mood = mood ?: preference.mood
            }
        }
    }
}
