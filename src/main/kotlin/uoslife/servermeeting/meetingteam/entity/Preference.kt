package uoslife.servermeeting.meetingteam.entity

import jakarta.persistence.*
import uoslife.servermeeting.meetingteam.entity.enums.TeamMood
import uoslife.servermeeting.user.entity.enums.ReligionType
import uoslife.servermeeting.user.entity.enums.SmokingType
import uoslife.servermeeting.user.entity.enums.SpiritAnimalType
import uoslife.servermeeting.user.entity.enums.StudentType

@Entity
class Preference(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id : Long? = null,

    var ageMin: Int? = null,
    var ageMax: Int? = null,
    var heightMin: Int? = null,
    var heightMax: Int? = null,
    @Enumerated(value = EnumType.STRING)
    var studentType: StudentType? = null,
    var smoking:List<SmokingType>? = null, //todo: List는 엔티티에 어떻게 저장할지
    var spiritAnimal: List<SpiritAnimalType>? = null,
    var mbti: String? = null,
    var mood: TeamMood? = null,

    var weight:String?=null,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_team_id")
    var meetingTeam: MeetingTeam?=null,
)
