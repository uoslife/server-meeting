package uoslife.servermeeting.meetingteam.dto.request

import jakarta.validation.constraints.NotBlank
import uoslife.servermeeting.meetingteam.entity.Preference
import uoslife.servermeeting.meetingteam.entity.enums.TeamMood
import uoslife.servermeeting.user.entity.enums.*

class MeetingTeamPreferenceUpdateRequest(
    @NotBlank val ageMin: Int,
    @NotBlank val ageMax: Int,
    val heightMin: Int?,
    val heightMax: Int?,
    val studentType: List<StudentType>?,
    @NotBlank val university: List<String>,
    val religion: List<ReligionType>?,
    val smoking: List<SmokingType>?,
    val spiritAnimal: List<SpiritAnimalType>?,
    // TODO: mbti 형식 어떻게 받을지 지정
    val mbti: List<String>?,
    val mood: TeamMood?,
) {
    fun toSinglePreference(): Preference {
        return Preference(
            ageMin = ageMin,
            ageMax = ageMax,
            heightMin = heightMin,
            heightMax = heightMax,
            studentType = studentType,
            university = university,
            religion = religion,
            smoking = smoking,
            spiritAnimal = spiritAnimal,
            mbti = mbti,
        )
    }

    fun toTriplePreference(): Preference {
        return Preference(
            ageMin = ageMin,
            ageMax = ageMax,
            university = university,
            mood = mood,
        )
    }
}
