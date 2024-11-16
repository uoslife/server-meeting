package uoslife.servermeeting.meetingteam.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import uoslife.servermeeting.match.dto.response.MatchedInformation
import uoslife.servermeeting.match.dto.response.MatchedMeetingTeamInformationGetResponse
import uoslife.servermeeting.meetingteam.entity.Information
import uoslife.servermeeting.meetingteam.entity.Preference
import uoslife.servermeeting.meetingteam.entity.enums.TeamMood
import uoslife.servermeeting.meetingteam.entity.enums.TeamType
import uoslife.servermeeting.meetingteam.entity.enums.Weight
import uoslife.servermeeting.user.entity.enums.*

data class MeetingTeamInformationGetResponse(
    @field:NotNull @Schema(description = "팀 타입", example = "SINGLE") val teamType: TeamType,
    @Schema(description = "팀 이름", example = "팀 이름(1:1인 경우 null)") val teamName: String?,
    @field:NotNull @Schema(description = "성별", example = "MALE") val gender: GenderType,
    @Schema(description = "팀에 속한 유저 정보") val opponentLeaderProfile: UserProfile?,
    @Schema(description = "질문 응답값") val information: InformationDto? = null,
    @Schema(description = "상대방 선호 응답값") val preference: PreferenceDto?,
    @Schema(description = "상대에게 전하는 메세지") val message: String?
) {
    fun toMatchedMeetingTeamInformationGetResponse(): MatchedMeetingTeamInformationGetResponse {
        return MatchedMeetingTeamInformationGetResponse(
            teamType = teamType,
            teamName = teamName,
            gender = gender,
            leaderProfile = opponentLeaderProfile,
            information = MatchedInformation(gender = information?.gender),
            message = message
        )
    }
}

data class PreferenceDto(
    var ageMin: Int? = null,
    var ageMax: Int? = null,
    var heightMin: Int? = null,
    var heightMax: Int? = null,
    var smoking: List<SmokingType>? = null, // todo: List는 엔티티에 어떻게 저장할지
    var appearanceType: List<AppearanceType>? = null,
    var eyelidType: List<EyelidType>? = null,
    var mbti: String? = null,
    var mood: TeamMood? = null,
    var weight: Weight? = null,
    @Schema(description = "피하고 싶은 학번", example = "19") val avoidanceNumber: Int?,
    @Schema(description = "피하고 싶은 학과", example = "통계학과") val avoidanceDepartment: String?,
) {
    companion object {
        fun valueOf(preference: Preference): PreferenceDto {
            return PreferenceDto(
                ageMin = preference.ageMin,
                ageMax = preference.ageMax,
                heightMax = preference.heightMax,
                heightMin = preference.heightMin,
                smoking = preference.smoking,
                appearanceType = preference.appearanceType,
                eyelidType = preference.eyelidType,
                mbti = preference.mbti,
                mood = preference.mood,
                weight = preference.weight,
                avoidanceNumber = preference.avoidanceNumber,
                avoidanceDepartment = preference.avoidanceDepartment
            )
        }
    }
}

data class InformationDto(val gender: GenderType?) {
    companion object {
        fun valueOf(information: Information): InformationDto {
            return InformationDto(gender = information.gender)
        }
    }
}

data class UserProfile(
    @field:NotNull @Schema(description = "유저 이름", example = "이름") val name: String?,
    @Schema(description = "유저 전화번호", example = "01012341234") val phoneNumber: String?,
    @field:NotNull @Schema(description = "유저 나이", example = "20") val age: Int?,
    @Schema(description = "유저 키", example = "180") val height: Int?,
    @field:NotNull @Schema(description = "학과", example = "경영학부") val department: String?,
    @field:NotNull @Schema(description = "카카오톡 ID", example = "kakaoId") val kakaoTalkId: String?,
    @Schema(description = "흡연 여부", example = "TRUE") val smoking: SmokingType?,
    @Schema(description = "mbti", example = "ENFP") val mbti: String?,
    @Schema(
        description = "관심사",
        example =
            "[{ \"name\": \"여행\", \"isDefault\": true }, { \"name\": \"맛집 탐방하기\", \"isDefault\": false }]"
    )
    val interest: List<String>?,
)
