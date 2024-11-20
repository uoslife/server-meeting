package uoslife.servermeeting.user.dto.response

import uoslife.servermeeting.user.entity.User
import uoslife.servermeeting.user.entity.enums.GenderType
import uoslife.servermeeting.user.entity.enums.StudentType

data class UserSimpleResponse(
    val email: String,
    val phoneNumber: String?,
    val name: String?,
    val kakaoTalkId: String?,
    val gender: GenderType?,
    val studentType: StudentType?,
) {
    companion object {
        fun valueOf(user: User): UserSimpleResponse {
            return UserSimpleResponse(
                email = user.email!!,
                phoneNumber = user.phoneNumber,
                name = user.name,
                kakaoTalkId = user.kakaoTalkId,
                gender = user.gender,
                studentType = user.studentType
            )
        }
    }
}
