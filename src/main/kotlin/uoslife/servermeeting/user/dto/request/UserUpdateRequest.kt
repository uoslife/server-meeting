package uoslife.servermeeting.user.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import uoslife.servermeeting.user.entity.User
import uoslife.servermeeting.user.entity.UserAdditionInformation
import uoslife.servermeeting.user.entity.enums.*

data class UserUpdateRequest(
    @Schema(description = "이름", example = "유현승", nullable = false) @field:NotNull val name: String,
    @Schema(description = "성별", example = "MALE", nullable = false)
    @field:NotNull
    val gender: GenderType,
    @Schema(description = "전화번호", example = "01047324348")
    @field:Size(max = 11)
    val phoneNumber: String?,
    @Schema(description = "나이", example = "26", nullable = false) @field:NotNull val age: Int,
    @Schema(description = "카카오톡 아이디", example = "__uhyun", nullable = false)
    @field:NotNull
    val kakaoTalkId: String,
    @Schema(description = "학과", example = "컴퓨터과학부", nullable = false)
    @field:NotNull
    val department: String,
    @Schema(description = "학번", example = "18", nullable = false) val studentNumber: Int?,
    @Schema(description = "키", example = "178") val height: Int?,
    @Schema(description = "흡연 여부", example = "FALSE") val smoking: SmokingType?,
    @Schema(description = "MBTI", example = "INFP") val mbti: String?,
    @Schema(description = "흥미", example = "[\"EXERCISE\", \"MUSIC\"]")
    val interest: List<InterestType>?,
) {
    fun toUserPersonalInformation(existingUser: User, validMBTI: String?): UserAdditionInformation {
        return UserAdditionInformation(
            smoking = smoking ?: existingUser.userAdditionInformation.smoking,
            mbti = validMBTI ?: existingUser.userAdditionInformation.mbti,
            interest = interest ?: existingUser.userAdditionInformation.interest,
        )
    }
}
