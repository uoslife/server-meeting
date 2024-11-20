package uoslife.servermeeting.user.command

import uoslife.servermeeting.user.entity.enums.AppearanceType
import uoslife.servermeeting.user.entity.enums.EyelidType
import uoslife.servermeeting.user.entity.enums.SmokingType

class UpdateUserCommand {

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

    data class UpdateUserProfile(
        val userId: Long,
    )
}
