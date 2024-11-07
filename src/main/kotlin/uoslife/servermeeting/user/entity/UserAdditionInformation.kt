package uoslife.servermeeting.user.entity

import java.io.Serializable
import uoslife.servermeeting.user.entity.enums.*

data class UserAdditionInformation( // 시즌 정책에 따라 바뀌는 컬럼들
    var smoking: SmokingType? = null,
    var mbti: String? = null,
    var interest: List<InterestType>? = null, // 취미

/* 시즌 5에선 제외된 컬럼입니다.
var studentType: StudentType = StudentType.UNDERGRADUATE,
var religion: ReligionType? = null,
var university: University? = null,
var drinkingMin: Int? = null,
var drinkingMax: Int? = null,
var spiritAnimal: List<SpiritAnimalType>? = null,
 */
) : Serializable
