package uoslife.servermeeting.domain.user.application.request

import jakarta.validation.constraints.NotBlank
import uoslife.servermeeting.domain.user.domain.entity.enums.DepartmentNameType
import uoslife.servermeeting.domain.user.domain.entity.enums.GenderType
import uoslife.servermeeting.domain.user.domain.entity.enums.StudentType

data class UserUpdateRequest(
    @NotBlank
    val birthYear: Int,
    @NotBlank
    val gender: GenderType,
    @NotBlank
    val name: String,
    @NotBlank
    val department: DepartmentNameType,
    @NotBlank
    val studentType: StudentType,
    @NotBlank
    val smoking: Boolean,
    @NotBlank
    val spiritAnimal: String,
    @NotBlank
    val mbti: String,
    @NotBlank
    val interest: String,
    @NotBlank
    val height: Int,
    @NotBlank
    val nickname: String
)
