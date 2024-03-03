package uoslife.servermeeting.meetingteam.entity

import jakarta.validation.constraints.NotBlank
import uoslife.servermeeting.meetingteam.entity.enums.TeamMood
import uoslife.servermeeting.user.entity.enums.ReligionType
import uoslife.servermeeting.user.entity.enums.SmokingType
import uoslife.servermeeting.user.entity.enums.SpiritAnimalType
import uoslife.servermeeting.user.entity.enums.StudentType

data class Preference(
    var ageMin: Int? = 0,
    var ageMax: Int? = 0,
    var heightMin: Int? = 0,
    var heightMax: Int? = 0,
    var studentType: List<StudentType>? = null,
    var university: List<String>? = null,
    var religion: List<ReligionType>? = null,
    var smoking: List<SmokingType>? = null,
    var spiritAnimal: List<SpiritAnimalType>? = null,
    var mbti: List<String>? = null,
    var mood: TeamMood?? = null,
)
