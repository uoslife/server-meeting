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
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_team_id")
    var meetingTeam: MeetingTeam? = null,
    var ageMin: Int? = null,
    var ageMax: Int? = null,
    var heightMin: Int? = null,
    var heightMax: Int? = null,
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "preference_smoking",
        joinColumns = [JoinColumn(name = "preference_id")]
    )
    @Enumerated(EnumType.STRING)
    var smoking: MutableList<SmokingType>? = null,
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "preference_appearanceType",
        joinColumns = [JoinColumn(name = "preference_id")]
    )
    @Enumerated(EnumType.STRING)
    var appearanceType: MutableList<AppearanceType>? = null,
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "preference_eyelidType",
        joinColumns = [JoinColumn(name = "preference_id")]
    )
    @Enumerated(EnumType.STRING)
    var eyelidType: MutableList<EyelidType>? = null,
    var mbti: String? = null,
    var mood: TeamMood? = null,
    var weight: Weight? = null,
    var avoidanceNumber: Int? = null,
    var avoidanceDepartment: String? = null,
)
