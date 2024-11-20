package uoslife.servermeeting.user.dto.response

import uoslife.servermeeting.user.entity.enums.GenderType
import uoslife.servermeeting.user.entity.enums.StudentType

data class UserResponse(
    val email: String,
    val phoneNumber: String?,
    val name: String?,
    val kakaoTalkId: String?,
    val gender: GenderType?,
    val studentType: StudentType?,
)
