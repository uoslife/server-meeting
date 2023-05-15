package uoslife.servermeeting.entities

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "informations")
class Information(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "information_id")
    val informationId: Long,

    @OneToOne(cascade = [CascadeType.ALL])
    @PrimaryKeyJoinColumn
    val team: MeetingTeam,

    @Column(name = "meeting_location", nullable = false)
    val meetingLocation: String,

    @Column(name = "meeting_time", nullable = false)
    val meetingTime: LocalDate,

    @Column(name = "age", nullable = false)
    val age: String,

    @Column(name = "height", nullable = false)
    val height: String,

    @Column(name = "filter_info", nullable = false, length = 255)
    val filterInfo: String,

    @Column(name = "distance_info", nullable = false, length = 255)
    val distanceInfo: String
)