package uoslife.servermeeting.entities

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "information")
class Information(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false, unique = true)
    var id: Long? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    var meetingTeam: MeetingTeam? = null,

    @Column(nullable = false)
    var meetingLocation: String,

    @Column(nullable = false)
    var meetingTime: LocalDateTime,

    @Column(nullable = false, length = 255)
    var filterInfo: String,

    @Column(nullable = false, length = 255)
    var distanceInfo: String,

    var aboutMe: String,
)
