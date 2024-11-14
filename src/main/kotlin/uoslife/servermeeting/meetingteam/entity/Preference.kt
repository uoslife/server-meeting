package uoslife.servermeeting.meetingteam.entity

import jakarta.persistence.*
import uoslife.servermeeting.meetingteam.entity.enums.TeamMood
import uoslife.servermeeting.meetingteam.entity.enums.Weight
import uoslife.servermeeting.user.entity.enums.AppearanceType
import uoslife.servermeeting.user.entity.enums.EyelidType
import uoslife.servermeeting.user.entity.enums.SmokingType

@Entity
class Preference(
    @Id @GeneratedValue(strategy = GenerationType.AUTO) var id: Long? = null,
    var ageMin: Int? = null,
    var ageMax: Int? = null,
    var heightMin: Int? = null,
    var heightMax: Int? = null,
    var smoking: List<SmokingType>? = null, // todo: List는 엔티티에 어떻게 저장할지
    var appearanceType: List<AppearanceType>? = null,
    var eyelidType: List<EyelidType>? = null,
    var mbti: String? = null,
    var mood: TeamMood? = null,
    var weight: Weight? = null,
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_team_id")
    var meetingTeam: MeetingTeam? = null,
    var avoidanceNumber: Int? = null,
    var avoidanceDepartment: String? = null,
)
