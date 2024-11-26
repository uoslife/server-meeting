package uoslife.servermeeting.user.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import uoslife.servermeeting.user.command.UserCommand
import uoslife.servermeeting.user.entity.enums.*

data class UserUpdateRequest(
    @Schema(description = "나이", example = "25") val age: Int? = null, // 프론트에서 변환해서 전송
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
    fun toUpdateUserInformationCommand(userId: Long): UserCommand.UpdateUserInformation {
        return UserCommand.UpdateUserInformation(
            userId = userId,
            smoking = this.smoking,
            mbti = this.mbti,
            interest = this.interest,
            height = this.height,
            age = this.age,
            studentNumber = this.studentNumber,
            department = this.department,
            eyelidType = this.eyelidType,
            appearanceType = this.appearanceType,
            studentType = this.studentType
        )
    }
}
