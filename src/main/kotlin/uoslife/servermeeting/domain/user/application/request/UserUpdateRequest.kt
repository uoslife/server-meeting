package uoslife.servermeeting.domain.user.application.request

import uoslife.servermeeting.domain.user.domain.entity.enums.DepartmentNameType
import uoslife.servermeeting.domain.user.domain.entity.enums.GenderType
import uoslife.servermeeting.domain.user.domain.entity.enums.StudentType

data class UserUpdateRequest(
    val birthYear: Int,
    val gender: GenderType,
    val name: String,
    val department: DepartmentNameType,
    val studentType: StudentType,
    val smoking: Boolean,
    val spiritAnimal: String,
    val mbti: String,
    val interest: String,
    val height: Int,
    val nickname: String
)
