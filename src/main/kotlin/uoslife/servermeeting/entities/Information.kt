package uoslife.servermeeting.entities

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "information")
class Information(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false, unique = true)
    val id: Long? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    val meetingTeam: MeetingTeam? = null,

    @Column(nullable = false)
    val meetingLocation: String,

    @Column(nullable = false)
    val meetingTime: LocalDateTime,

    @Column(nullable = false, length = 255)
    val filterInfo: String,

    @Column(nullable = false, length = 255)
    val distanceInfo: String,
)
