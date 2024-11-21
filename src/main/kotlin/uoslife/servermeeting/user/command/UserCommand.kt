package uoslife.servermeeting.user.command

import uoslife.servermeeting.user.entity.enums.AppearanceType
import uoslife.servermeeting.user.entity.enums.EyelidType
import uoslife.servermeeting.user.entity.enums.GenderType
import uoslife.servermeeting.user.entity.enums.SmokingType

class UserCommand {

    data class UpdateUserInformation(
        val userId: Long,
        val smoking: SmokingType?,
        var mbti: String?,
        val interest: List<String>?,
        val height: Int?,
        val age: Int?,
        val studentNumber: Int?,
        val department: String?,
        val eyelidType: EyelidType?,
        val appearanceType: AppearanceType?,
    )

    data class UpdateUserPersonalInformation(
        val userId: Long,
        val name: String?,
        val phoneNumber: String?,
        val gender: GenderType?,
        val kakaoTalkId: String?,
    )
}
