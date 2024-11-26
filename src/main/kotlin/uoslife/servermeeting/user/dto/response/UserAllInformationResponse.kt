package uoslife.servermeeting.user.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import uoslife.servermeeting.user.entity.User
import uoslife.servermeeting.user.entity.enums.*

data class UserAllInformationResponse(
    @Schema(description = "이름", example = "유현승") val name: String?,
    @Schema(description = "성별", example = "MALE") val genderType: GenderType?,
    @Schema(description = "나이", example = "26") val age: Int?,
    @Schema(description = "전화번호", example = "01047324348") val phoneNumber: String?,
    @Schema(description = "카카오톡 아이디", example = "__uhyun") val kakaoTalkId: String?,
    @Schema(description = "이메일", example = "zaza0804@uos.ac.kr") val email: String?,
    @Schema(description = "학과", example = "컴퓨터과학부") val department: String?,
    @Schema(description = "학번", example = "19") val studentNumber: Int?,
    @Schema(description = "키", example = "178") val height: Int?,
    @Schema(description = "흡연 여부", example = "FALSE") val smoking: SmokingType?,
    @Schema(description = "MBTI", example = "INFP") val mbti: String?,
    @Schema(description = "흥미", example = "[\"EXERCISE\", \"MUSIC\"]") val interest: List<String>?,
    @Schema(description = "외모1", example = "ARAB") val appearanceType: AppearanceType?,
    @Schema(description = "외모2", example = "DOUBLE") val eyelidType: EyelidType?,
    @Schema(description = "학적", example = "UNDERGRADUATE") val studentType: StudentType?,
) {
    companion object {
        fun valueOf(user: User): UserAllInformationResponse {
            return UserAllInformationResponse(
                name = user.name,
                genderType = user.gender,
                phoneNumber = user.phoneNumber,
                kakaoTalkId = user.kakaoTalkId,
                email = user.email,
                age = user.userInformation?.age,
                department = user.userInformation?.department,
                height = user.userInformation?.height,
                smoking = user.userInformation?.smoking,
                interest = user.userInformation?.interest,
                mbti = user.userInformation?.mbti,
                studentNumber = user.userInformation?.studentNumber,
                appearanceType = user.userInformation?.appearanceType,
                eyelidType = user.userInformation?.eyelidType,
                studentType = user.userInformation?.studentType
            )
        }
    }
}
