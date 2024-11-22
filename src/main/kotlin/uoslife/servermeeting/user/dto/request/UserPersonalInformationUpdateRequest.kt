package uoslife.servermeeting.user.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Size
import uoslife.servermeeting.user.command.UserCommand
import uoslife.servermeeting.user.entity.enums.GenderType

data class UserPersonalInformationUpdateRequest(
    @Schema(description = "이름", example = "유현승") val name: String? = null,
    @Schema(description = "전화번호", example = "01047324348")
    @field:Size(max = 11)
    val phoneNumber: String? = null,
    @Schema(description = "성별(한번만 설정 가능)", example = "MALE") val genderType: GenderType? = null,
    @Schema(description = "카카오톡 아이디", example = "__uhyun", nullable = true)
    val kakaoTalkId: String? = null,
) {
    fun toUpdateUserPersonalInformationCommand(
        userId: Long
    ): UserCommand.UpdateUserPersonalInformation {
        return UserCommand.UpdateUserPersonalInformation(
            userId = userId,
            name = this.name,
            phoneNumber = this.phoneNumber,
            kakaoTalkId = this.kakaoTalkId,
            gender = this.genderType,
        )
    }
}
