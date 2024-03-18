package uoslife.servermeeting.global.auth.dto.response

import java.time.LocalDate

data class UserProfileVO(
    val id: Long?,
    val name: String,
    val nickname: String?,
    val birthday: LocalDate?,
    val phone: String,
    val avatarUrl: String?,
    var isVerified: Boolean? = null,
    var degree: String? = null,
    var enrollmentStatus: String? = null,
    var studentId: String? = null,
    var departmentName: String? = null,
    var collegeName: String? = null
)
