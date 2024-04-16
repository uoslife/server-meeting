package uoslife.servermeeting.user.dto.response

import uoslife.servermeeting.user.entity.enums.*
import uoslife.servermeeting.verification.dto.University

data class UserFindResponseDto(
    val name: String?,
    val genderType: GenderType?,
    val phoneNumber: String?,
    val age: Int?,
    val kakaoTalkId: String?,
    val department: String?,
    val studentType: StudentType?,
    val height: Int?,
    val religion: ReligionType?,
    val drinkingMin: Int?,
    val drinkingMax: Int?,
    val smoking: SmokingType?,
    val spiritAnimal: List<SpiritAnimalType>?,
    val mbti: String?,
    val interest: List<InterestType>?,
    val university: University?,
)
