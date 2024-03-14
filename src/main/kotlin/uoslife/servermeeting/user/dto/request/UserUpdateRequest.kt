package uoslife.servermeeting.user.dto.request

import jakarta.validation.constraints.NotBlank
import uoslife.servermeeting.user.entity.enums.*

data class UserUpdateRequest(
    val phoneNumber: String?,
    val name: String?,
    val email: String?,
    val kakaoTalkId: String?,
    val age: Int?,
    val gender: GenderType?,
    val height: Int?,
    val studentType: StudentType?,
    val department: String?,
    val religion: ReligionType?,
    val drinkingMin: Int?,
    val drinkingMax: Int?,
    val smoking: SmokingType?,
    val spiritAnimal: List<SpiritAnimalType>?,
    val mbti: String?,
    val interest: List<InterestType>?,
    val message: String?,
)
