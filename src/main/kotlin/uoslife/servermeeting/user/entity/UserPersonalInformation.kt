package uoslife.servermeeting.user.entity

import java.io.Serializable
import uoslife.servermeeting.user.entity.enums.*
import uoslife.servermeeting.verification.dto.University

data class UserPersonalInformation(
    var age: Int? = 0,
    var gender: GenderType = GenderType.MALE,
    var height: Int? = 0,
    var kakaoTalkId: String? = null,
    var studentType: StudentType? = null,
    // TODO: 이메일 인증 절차 때 user 생성하면서 함께 넣을 것!
    var university: University? = null,
    var department: String? = null,
    var religion: ReligionType? = null,
    var drinkingMin: Int? = null,
    var drinkingMax: Int? = null,
    var smoking: SmokingType? = null,
    var spiritAnimal: List<SpiritAnimalType>? = null,
    var mbti: String? = null,
    var interest: List<InterestType>? = null,
    var message: String? = null,
) : Serializable
