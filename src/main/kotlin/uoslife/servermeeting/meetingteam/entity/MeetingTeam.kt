package uoslife.servermeeting.meetingteam.entity

import jakarta.persistence.*
import uoslife.servermeeting.global.common.BaseEntity
import uoslife.servermeeting.match.entity.Match
import uoslife.servermeeting.meetingteam.entity.enums.TeamType
import uoslife.servermeeting.payment.entity.Payment
import uoslife.servermeeting.user.entity.enums.GenderType

@Entity
@Table(name = "meeting_team")
class MeetingTeam(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long? = null,
    @Column(nullable = false) var season: Int,
    @Column(unique = true) var code: String? = null,
    var name: String? = null,
    var course: String? = null,
    @Column(nullable = false) @Enumerated(EnumType.STRING) var type: TeamType,
    @Enumerated(EnumType.STRING) var gender: GenderType,
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "maleTeam") var maleMatch: Match? = null,
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "femaleTeam") var femaleMatch: Match? = null,
    @OneToMany(
        fetch = FetchType.LAZY,
        mappedBy = "team",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    var userTeams: MutableList<UserTeam> = mutableListOf(),
    @OneToOne(
        fetch = FetchType.LAZY,
        mappedBy = "meetingTeam",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    var preference: Preference? = null,
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "meetingTeam")
    var payments: MutableList<Payment>? = null,
) : BaseEntity()
