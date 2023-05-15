package uoslife.servermeeting.entities

import jakarta.persistence.*

@Entity
@Table(name = "preferences")
class Preference(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false, unique = true)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    val meetingTeam: MeetingTeam? = null,

    @Column(nullable = false)
    val ageRangePreference: String,

    @Column(nullable = false)
    val heightRangePreference: String,

    @Column(nullable = false)
    val filterCondition: String,

    @Column(nullable = false)
    val distanceCondition: String,
)
