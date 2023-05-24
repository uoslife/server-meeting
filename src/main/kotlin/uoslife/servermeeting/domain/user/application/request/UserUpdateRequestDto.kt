package uoslife.servermeeting.domain.user.application.request

import jakarta.validation.constraints.Pattern
import uoslife.servermeeting.domain.user.domain.entity.enums.DepartmentNameType
import uoslife.servermeeting.domain.user.domain.entity.enums.GenderType
import uoslife.servermeeting.domain.user.domain.entity.enums.StudentType

data class UserUpdateRequestDto(
    val birthYear: Int?,
    val gender: GenderType?,
    val name: String?,
    val department: DepartmentNameType?,
    val studentType: StudentType?,
    val smoking: Boolean?,
    val spiritAnimal: String?,
    val mbti: String?,
    val interest: String?,
    val height: Int?,
    val nickname: String?
) {
    fun hasChanges(): Boolean {
        return birthYear != null ||
            gender != null ||
            name != null ||
            department != null ||
            studentType != null ||
            smoking != null ||
            spiritAnimal != null ||
            mbti != null ||
            interest != null ||
            height != null ||
            nickname != null
    }
}

