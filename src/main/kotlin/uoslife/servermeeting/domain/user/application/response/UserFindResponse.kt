package uoslife.servermeeting.domain.user.application.response

import uoslife.servermeeting.domain.user.domain.entity.User
import uoslife.servermeeting.domain.user.domain.entity.enums.DepartmentNameType
import uoslife.servermeeting.domain.user.domain.entity.enums.GenderType
import uoslife.servermeeting.domain.user.domain.entity.enums.StudentType
import java.util.UUID

data class UserFindResponseDto(
    val id: UUID,
    val birthYear: Number?,
    val gender: GenderType?,
    val department: DepartmentNameType?,
    val kakaoTalkId: String?,
    val studentType: StudentType?,
    val smoking: Boolean?,
    val spirit_animal: String?,
    val mbti: String?,
    val interest: String?,
    val height: Number?,
    val nickname: String
)

fun User.toResponse() = UserFindResponseDto(
    id = id!!,
    birthYear = userPersonalInformation.birthYear,
    gender = userPersonalInformation.gender,
    department = userPersonalInformation.department,
    kakaoTalkId = userPersonalInformation.kakaoTalkId,
    studentType = userPersonalInformation.studentType,
    smoking = userPersonalInformation.smoking,
    spirit_animal = userPersonalInformation.spiritAnimal,
    mbti = userPersonalInformation.mbti,
    interest = userPersonalInformation.interest,
    height = userPersonalInformation.height,
    nickname = nickname,
)
