package uoslife.servermeeting.user.entity

import uoslife.servermeeting.user.entity.enums.DepartmentNameType
import uoslife.servermeeting.user.entity.enums.StudentType

data class UserPersonalInformation(
    var birthYear: Int? = null,
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
