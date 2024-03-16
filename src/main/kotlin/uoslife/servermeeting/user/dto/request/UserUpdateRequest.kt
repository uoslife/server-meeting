package uoslife.servermeeting.user.dto.request

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import uoslife.servermeeting.user.entity.enums.*

data class UserUpdateRequest(
    @field:NotNull val name: String,
    @field:NotNull val gender: GenderType,
    // TODO: PHONENUMBER UI 추가 요청
    @field:NotNull @field:Size(max = 11) val phoneNumber: String,
    @field:NotNull val age: Int,
    @field:NotNull val kakaoTalkId: String,
    @field:NotNull val department: String,
    @field:NotNull val studentType: StudentType,
    val height: Int?,
    val religion: ReligionType?,
    val smoking: SmokingType?,
    val drinkingMin: Int?,
    val drinkingMax: Int?,
    val spiritAnimal: List<SpiritAnimalType>?,
    val mbti: String?,
    val interest: List<InterestType>?,
)
