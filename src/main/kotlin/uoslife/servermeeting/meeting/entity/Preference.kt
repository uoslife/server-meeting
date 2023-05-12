package uoslife.servermeeting.meeting.entity

import jakarta.persistence.*

@Entity
@Table(name = "preferences")
class Preference(
    @Id
    @Column(name = "preference_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val preferenceId: Long,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    val team: MeetingTeam,

    @Column(name = "age_range_preference", nullable = false)
    val ageRangePreference: String,
    @Column(name = "height_range_preference", nullable = false)
    val heightRangePreference: String,
    @Column(name = "filter_condition", nullable = false)
    val filterCondition: String,
    @Column(name = "distance_condition", nullable = false)
    val distanceCondition: String
)