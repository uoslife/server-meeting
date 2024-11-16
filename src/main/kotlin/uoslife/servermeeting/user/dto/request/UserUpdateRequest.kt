package uoslife.servermeeting.user.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import uoslife.servermeeting.meetingteam.exception.PreconditionFailedException
import uoslife.servermeeting.user.entity.User
import uoslife.servermeeting.user.entity.enums.*

data class UserUpdateRequest(
    @Schema(description = "이름", example = "유현승") val name: String? = null,
    @Schema(description = "전화번호", example = "01047324348")
    @field:Size(max = 11)
    val phoneNumber: String? = null,
    @Schema(description = "성별(한번만 설정 가능)", example = "MALE") val genderType: GenderType? = null,
    @Schema(description = "나이", example = "25") val age: Int? = null, // 프론트에서 변환해서 전송
    @Schema(description = "카카오톡 아이디", example = "__uhyun", nullable = false)
    @field:NotNull
    val kakaoTalkId: String? = null,
    @Schema(description = "학과", example = "컴퓨터과학부", nullable = false)
    @field:NotNull
    val department: String? = null,
    @Schema(description = "학번", example = "18", nullable = false) val studentNumber: Int?,
    @Schema(description = "키", example = "178") val height: Int?,
    @Schema(description = "흡연 여부", example = "FALSE") val smoking: SmokingType?,
    @Schema(description = "MBTI", example = "INFP") val mbti: String?,
    @Schema(description = "흥미", example = "[\"EXERCISE\", \"MUSIC\"]")
    val interest: List<String>? = null,
    @Schema(description = "외모1", example = "ARAB") val appearanceType: AppearanceType? = null,
    @Schema(description = "외모2", example = "DOUBLE") val eyelidType: EyelidType? = null,
    @Schema(description = "학적", example = "UNDERGRADUATE") val studentType: StudentType? = null,
) {
    fun updateUserInformation(existingUser: User, validMBTI: String?) {
        val existingUserInfo = existingUser.userInformation ?: throw PreconditionFailedException()
        existingUserInfo.smoking = smoking ?: existingUserInfo.smoking
        existingUserInfo.mbti = validMBTI ?: existingUserInfo.mbti
        existingUserInfo.interest = interest ?: existingUserInfo.interest
        existingUserInfo.height = height ?: existingUserInfo.height
        existingUserInfo.age = age ?: existingUserInfo.age
        existingUserInfo.studentNumber = studentNumber ?: existingUserInfo.studentNumber
        existingUserInfo.department = department ?: existingUserInfo.department
        existingUserInfo.eyelidType = eyelidType ?: existingUserInfo.eyelidType
        existingUserInfo.appearanceType = appearanceType ?: existingUserInfo.appearanceType
    }
}
