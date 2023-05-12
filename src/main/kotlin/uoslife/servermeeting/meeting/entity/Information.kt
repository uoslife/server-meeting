package uoslife.servermeeting.meeting.entity

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "informations")
class Information(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "information_id", nullable = false, updatable = false)
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

    @Column(name = "filter_info", nullable = false)
    val filterInfo: String,

    @Column(name = "distance_info", nullable = false)
    val distanceInfo: String,

    @Column(name = "about_me", nullable = false)
    val aboutMe: String,
)