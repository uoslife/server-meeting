package uoslife.servermeeting.user.dto.request

import jakarta.validation.constraints.NotBlank
import uoslife.servermeeting.user.entity.enums.*

data class UserUpdateRequest(
    @NotBlank val name: String,
    @NotBlank val gender: GenderType,
    // TODO: PHONENUMBER UI 추가 요청
    @NotBlank val phoneNumber: String,
    @NotBlank val age: Int,
    @NotBlank val height: Int,
    @NotBlank val kakaoTalkId: String,
    @NotBlank val department: String,
    @NotBlank val studentType: StudentType,
    val religion: ReligionType?,
    val smoking: SmokingType?,
    val drinkingMin: Int?,
    val drinkingMax: Int?,
    val spiritAnimal: List<SpiritAnimalType>?,
    val mbti: String?,
    val interest: List<InterestType>?,
)
