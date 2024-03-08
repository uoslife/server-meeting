package uoslife.servermeeting.user.dto.response

import uoslife.servermeeting.user.entity.User
import uoslife.servermeeting.user.entity.enums.InterestType
import uoslife.servermeeting.user.entity.enums.SmokingType
import uoslife.servermeeting.user.entity.enums.SpiritAnimalType
import uoslife.servermeeting.user.entity.enums.StudentType
import uoslife.servermeeting.verification.dto.University

data class UserFindResponseDto(
    val name: String?,
    val age: Int?,
    val height: Int?,
    val university: University?,
    val department: String?,
    val studentType: StudentType?,
    val kakaoTalkId: String?,
    val smoking: SmokingType?,
    val drinkingMin: Int?,
    val drinkingMax: Int?,
    val spiritAnimal: List<SpiritAnimalType>?,
    val mbti: String?,
    val interest: List<InterestType>?,
)

fun User.toResponse() =
    UserFindResponseDto(
        name = name,
        age = userPersonalInformation.age,
        height = userPersonalInformation.height,
        university = userPersonalInformation.university,
        department = userPersonalInformation.department,
        studentType = userPersonalInformation.studentType,
        kakaoTalkId = kakaoTalkId,
        smoking = userPersonalInformation.smoking,
        drinkingMin = userPersonalInformation.drinkingMin,
        drinkingMax = userPersonalInformation.drinkingMax,
        spiritAnimal = userPersonalInformation.spiritAnimal,
        mbti = userPersonalInformation.mbti,
        interest = userPersonalInformation.interest
    )
