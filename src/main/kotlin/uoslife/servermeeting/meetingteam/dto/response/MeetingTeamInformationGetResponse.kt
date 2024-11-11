package uoslife.servermeeting.meetingteam.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import uoslife.servermeeting.match.dto.response.MatchedMeetingTeamInformationGetResponse
import uoslife.servermeeting.meetingteam.entity.Information
import uoslife.servermeeting.meetingteam.entity.Preference
import uoslife.servermeeting.meetingteam.entity.enums.TeamType
import uoslife.servermeeting.user.dto.Interest
import uoslife.servermeeting.user.entity.enums.*

data class MeetingTeamInformationGetResponse(
    @field:NotNull @Schema(description = "팀 타입", example = "SINGLE") val teamType: TeamType,
    @Schema(description = "팀 이름", example = "팀 이름(1:1인 경우 null)") val teamName: String?,
    @field:NotNull @Schema(description = "성별", example = "MALE") val gender: GenderType,
    @Schema(description = "팀에 속한 유저 정보") val opponentLeaderProfile: UserProfile?,
    @Schema(description = "질문 응답값") val information: Information?,
    @Schema(description = "상대방 선호 응답값") val preference: Preference?,
    @Schema(description = "상대에게 전하는 메세지") val message: String?
) {
    fun toMatchedMeetingTeamInformationGetResponse(): MatchedMeetingTeamInformationGetResponse {
        return MatchedMeetingTeamInformationGetResponse(
            teamType = teamType,
            teamName = teamName,
            gender = gender,
            leaderProfile = opponentLeaderProfile,
            information = information?.toMatchedInformation(),
            message = message
        )
    }
}

data class UserProfile(
    @field:NotNull @Schema(description = "유저 이름", example = "이름") val name: String,
    @Schema(description = "유저 전화번호", example = "01012341234") val phoneNumber: String?,
    @field:NotNull @Schema(description = "유저 나이", example = "20") val age: Int,
    @Schema(description = "유저 키", example = "180") val height: Int?,
    @field:NotNull @Schema(description = "학과", example = "경영학부") val department: String,
    @field:NotNull @Schema(description = "카카오톡 ID", example = "kakaoId") val kakaoTalkId: String,
    @Schema(description = "흡연 여부", example = "TRUE") val smoking: SmokingType?,
    @Schema(description = "mbti", example = "ENFP") val mbti: String?,
    @Schema(description = "관심사", example = "[{ \"name\": \"여행\", \"isDefault\": true }, { \"name\": \"맛집 탐방하기\", \"isDefault\": false }]")
    val interests: MutableList<Interest>?,
)
