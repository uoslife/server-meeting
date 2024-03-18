package uoslife.servermeeting.meetingteam.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import uoslife.servermeeting.meetingteam.entity.Information
import uoslife.servermeeting.meetingteam.entity.Preference
import uoslife.servermeeting.meetingteam.entity.enums.TeamType
import uoslife.servermeeting.user.entity.enums.*
import uoslife.servermeeting.verification.dto.University

data class MeetingTeamInformationGetResponse(
    @field:NotNull @Schema(description = "팀 타입", example = "SINGLE") val teamType: TeamType,
    @Schema(description = "팀 이름", example = "팀 이름") val teamName: String?,
    @field:NotNull @Schema(description = "성별", example = "MALE") val gender: GenderType,
    @Schema(description = "팀에 속한 유저 정보") val teamUserList: List<UserProfile>?,
    @Schema(description = "질문 응답값") val information: Information?,
    @Schema(description = "상대방 선호 응답값") val preference: Preference?,
    @Schema(description = "상대에게 전하는 메세지") val message: String?
)

data class UserProfile(
    @field:NotNull @Schema(description = "유저 이름", example = "이름") val name: String,
    @field:NotNull @Schema(description = "유저 나이", example = "20") val age: Int,
    @Schema(description = "유저 키", example = "180") val height: Int?,
    @Schema(description = "대학", example = "UOS") val university: University?,
    @field:NotNull @Schema(description = "학과", example = "경영학부") val department: String,
    @field:NotNull
    @Schema(description = "학생 신분", example = "UNDERGRADUATE")
    val studentType: StudentType,
    @field:NotNull @Schema(description = "카카오톡 ID", example = "kakaoId") val kakaoTalkId: String,
    @Schema(description = "흡연 여부", example = "TRUE") val smoking: SmokingType?,
    @Schema(description = "종교", example = "CHRISTIAN") val religion: ReligionType?,
    @Schema(description = "한달 최소 음주량", example = "1") val drinkingMin: Int?,
    @Schema(description = "한달 최대 음주량", example = "10") val drinkingMax: Int?,
    @Schema(description = "동물상", example = "[DOG, CAT]") val spiritAnimal: List<SpiritAnimalType>?,
    @Schema(description = "mbti", example = "ENFP") val mbti: String?,
    @Schema(description = "흥미", example = "[BOOK, EXERCISE]") val interest: List<InterestType>?,
)
