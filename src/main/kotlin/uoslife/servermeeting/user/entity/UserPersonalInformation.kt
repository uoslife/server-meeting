package uoslife.servermeeting.user.entity

import java.io.Serializable
import uoslife.servermeeting.user.entity.enums.*

data class UserPersonalInformation(
    var age: Int = 0,
    var gender: GenderType = GenderType.MALE,
    var height: Int? = null,
    var studentType: StudentType = StudentType.UNDERGRADUATE,
    var university: University? = null,
    var department: String = "",
    var religion: ReligionType? = null,
    var drinkingMin: Int? = null,
    var drinkingMax: Int? = null,
    var smoking: SmokingType? = null,
    var spiritAnimal: List<SpiritAnimalType>? = null,
    var mbti: String? = null,
    var interest: List<InterestType>? = null, // 취미
) : Serializable
