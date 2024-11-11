package uoslife.servermeeting.meetingteam.entity

import uoslife.servermeeting.meetingteam.entity.enums.TeamMood
import uoslife.servermeeting.user.entity.enums.ReligionType
import uoslife.servermeeting.user.entity.enums.SmokingType
import uoslife.servermeeting.user.entity.enums.StudentType

data class Preference(
    var ageMin: Int? = null,
    var ageMax: Int? = null,
    var heightMin: Int? = null,
    var heightMax: Int? = null,
    var studentType: List<StudentType>? = null,
    var religion: List<ReligionType>? = null,
    var smoking: List<SmokingType>? = null,
    var drinkingMin: Int? = null,
    var drinkingMax: Int? = null,
    var mbti: String? = null,
    var mood: TeamMood? = null,
)
