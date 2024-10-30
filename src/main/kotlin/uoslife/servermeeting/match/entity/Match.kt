package uoslife.servermeeting.match.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import uoslife.servermeeting.meetingteam.entity.SingleMeetingTeam
import uoslife.servermeeting.meetingteam.entity.TripleMeetingTeam

@Entity
@Table(name = "match")
class Match(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false, unique = true)
    var id: Long? = null,
    var date: LocalDateTime? = null,
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "male_team_id", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    var maleTeam: SingleMeetingTeam,
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "female_team_id", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    var femaleTeam: SingleMeetingTeam,
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "triple_male_team_id", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    var tripleMaleTeam: TripleMeetingTeam,
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "triple_female_team_id",
        foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT)
    )
    var tripleFemaleTeam: TripleMeetingTeam,
)
