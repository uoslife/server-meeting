package uoslife.servermeeting.domain.user.application.response

import uoslife.servermeeting.domain.user.domain.entity.User
import uoslife.servermeeting.domain.user.domain.entity.enums.DepartmentNameType
import uoslife.servermeeting.domain.user.domain.entity.enums.GenderType
import uoslife.servermeeting.domain.user.domain.entity.enums.StudentType

data class UserFindResponseDto(
    val birthYear: Number?,
    val gender: GenderType?,
    val name: String?,
    val department: DepartmentNameType?,
    val studentType: StudentType?,
    val smoking: Boolean?,
    val spirit_animal: String?,
    val mbti: String?,
    val interest: String?,
    val height: Number?,
    val nickname: String?
)

fun User.toResponse() = UserFindResponseDto(
    birthYear = birthYear,
    gender = gender,
    name = name,
    department = department,
    studentType = studentType,
    smoking = smoking,
    spirit_animal = spiritAnimal,
    mbti = mbti,
    interest = interest,
    height = height,
    nickname = nickname,
)
