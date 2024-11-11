package uoslife.servermeeting.user.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import uoslife.servermeeting.user.entity.enums.*

data class UserFindResponse(
    @Schema(description = "이름", example = "유현승") val name: String?,
    @Schema(description = "이메일", example = "example@uos.ac.kr") val email: String?,
    @Schema(description = "성별", example = "MALE") val genderType: GenderType?,
    @Schema(description = "전화번호", example = "01047324348") val phoneNumber: String?,
    @Schema(description = "나이", example = "26") val age: Int?,
    @Schema(description = "카카오톡 아이디", example = "__uhyun") val kakaoTalkId: String?,
    @Schema(description = "학과", example = "컴퓨터과학부") val department: String?,
    @Schema(description = "키", example = "178") val height: Int?,
    @Schema(description = "흡연 여부", example = "FALSE") val smoking: SmokingType?,
    @Schema(description = "MBTI", example = "INFP") val mbti: String?,
    @Schema(description = "흥미", example = "[\"EXERCISE\", \"MUSIC\"]")
    val interest: List<InterestType>?,
    val tripleTeam: Boolean,
    val singleTeam: Boolean
)
