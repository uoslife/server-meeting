package uoslife.servermeeting.meetingteam.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import uoslife.servermeeting.match.dto.response.MatchedMeetingTeamInformationGetResponse
import uoslife.servermeeting.meetingteam.entity.Preference
import uoslife.servermeeting.meetingteam.entity.enums.TeamMood
import uoslife.servermeeting.meetingteam.entity.enums.TeamType
import uoslife.servermeeting.meetingteam.entity.enums.Weight
import uoslife.servermeeting.user.entity.enums.*

data class MeetingTeamInformationGetResponse(
    @field:NotNull @Schema(description = "팀 타입", example = "SINGLE") val teamType: TeamType,
    @Schema(description = "팀 이름", example = "팀 이름(1:1인 경우 null)") val teamName: String?,
    @Schema(description = "상대에게 전하는 데이트 코스", example = "밥먹기 (3:3인 경우 null)") val course: String?,
    @field:NotNull @Schema(description = "성별", example = "MALE") val gender: GenderType,
    @Schema(description = "팀에 속한 유저 정보") val meetingTeamUserProfiles: List<UserCardProfile>?,
    @Schema(description = "상대방 선호 응답값") val preference: PreferenceDto?,
) {
    fun toMatchedMeetingTeamInformationGetResponse(): MatchedMeetingTeamInformationGetResponse {
        return MatchedMeetingTeamInformationGetResponse(
            teamType = teamType,
            teamName = teamName,
            gender = gender,
            leaderProfile = meetingTeamUserProfiles,
            course = course
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

data class UserCardProfile(
    @Schema(description = "팅장 여부", example = "false") val isLeader: Boolean,
    @field:NotNull @Schema(description = "유저 이름", example = "이름") val name: String,
    @field:NotNull
    @Schema(description = "학적", example = "POSTGRADUATE")
    val studentType: StudentType,
    @Schema(description = "학과", example = "경영학부") val department: String?,
    @Schema(description = "학번", example = "18") val studentNumber: Int?,
    @field:NotNull @Schema(description = "유저 나이", example = "20") val age: Int,
    @Schema(description = "유저 키", example = "180") val height: Int?,
    @Schema(description = "mbti", example = "ENFP") val mbti: String?,
    @Schema(description = "외모", example = "NORMAL") val appearanceType: AppearanceType?,
    @Schema(description = "쌍커풀", example = "INNER") val eyelidType: EyelidType?,
    @Schema(description = "흡연 여부", example = "TRUE") val smoking: SmokingType?,
    @Schema(description = "관심사", example = "[{ \"name\": \"여행\" }, { \"name\": \"맛집 탐방하기\"}]")
    val interest: List<String>?,
    @field:NotNull @Schema(description = "카카오톡 ID", example = "kakaoId") val kakaoTalkId: String,
)
