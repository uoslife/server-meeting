package uoslife.servermeeting.domain.user.domain.entity

import uoslife.servermeeting.domain.user.domain.entity.enums.DepartmentNameType
import uoslife.servermeeting.domain.user.domain.entity.enums.GenderType
import uoslife.servermeeting.domain.user.domain.entity.enums.StudentType

data class UserPersonalInformation(

    var birthYear: Int? = null,

    var gender: GenderType = GenderType.MALE,

    var height: Int = 0,

    var kakaoTalkId: String? = null,

    var studentType: StudentType? = null,

    var university: String? = null,

    var department: DepartmentNameType? = null,

    var studentNumber: String? = null,

    var smoking: Boolean? = null,

    var spiritAnimal: String? = null,

    var mbti: String? = null,

    var interest: String? = null,

    )
