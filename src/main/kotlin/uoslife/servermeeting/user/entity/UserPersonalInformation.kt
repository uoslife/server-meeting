package uoslife.servermeeting.user.entity

import java.io.Serializable
import uoslife.servermeeting.user.entity.enums.*
import uoslife.servermeeting.verification.dto.University

data class UserPersonalInformation(
    var age: Int? = null,
    var gender: GenderType? = null,
    var height: Int? = null,
    var kakaoTalkId: String? = null,
    var studentType: StudentType? = null,
    var university: University? = null,
    var department: String? = null,
    var religion: ReligionType? = null,
    var drinkingMin: Int? = null,
    var drinkingMax: Int? = null,
    var smoking: SmokingType? = null,
    var spiritAnimal: List<SpiritAnimalType>? = null,
    var mbti: String? = null,
    var interest: List<InterestType>? = null, // 취미
) : Serializable
