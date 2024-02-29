package uoslife.servermeeting.global.auth.dto.request

import jakarta.validation.constraints.NotBlank
import uoslife.servermeeting.user.entity.UserPersonalInformation
import uoslife.servermeeting.verification.dto.University

data class SignUpRequestDTO(
    @field:NotBlank val phoneNumber: String,
    @field:NotBlank val nickname: String,
    @field:NotBlank val name: String,
    @field:NotBlank val email: String,
    @field:NotBlank val kakaoTalkId: String,
    var university: University? = null,
    var userPersonalInformation: UserPersonalInformation = UserPersonalInformation()
)
