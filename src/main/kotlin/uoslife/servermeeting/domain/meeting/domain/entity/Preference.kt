package uoslife.servermeeting.domain.meeting.domain.entity

import jakarta.persistence.*

@Entity
@Table(name = "preference")
class Preference(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false, unique = true)
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    var meetingTeam: MeetingTeam? = null,

    @Column(nullable = false)
    var ageRangePreference: String,

    @Column(nullable = false)
    var heightRangePreference: String,

    @Column(nullable = false)
    var filterCondition: String,

    @Column(nullable = false)
    var distanceCondition: String,
)
