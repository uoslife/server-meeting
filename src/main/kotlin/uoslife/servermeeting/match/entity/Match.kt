package uoslife.servermeeting.match.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import uoslife.servermeeting.global.common.BaseEntity
import uoslife.servermeeting.meetingteam.entity.MeetingTeam

@Entity
@Table(name = "match")
class Match(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false, unique = true)
    var id: Long? = null,
    var date: LocalDateTime? = null,
    @OneToOne(fetch = FetchType.LAZY) @JoinColumn(name = "male_team_id") var maleTeam: MeetingTeam,
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "female_team_id")
    var femaleTeam: MeetingTeam,
) : BaseEntity()
