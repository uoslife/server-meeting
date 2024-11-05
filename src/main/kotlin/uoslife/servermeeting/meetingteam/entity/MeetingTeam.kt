package uoslife.servermeeting.meetingteam.entity

import jakarta.persistence.*
import org.hibernate.query.sqm.tree.expression.Compatibility
import uoslife.servermeeting.global.common.BaseEntity
import uoslife.servermeeting.match.entity.Match
import uoslife.servermeeting.meetingteam.entity.enums.TeamType

@Entity
@Table(name = "meeting_team")
class MeetingTeam(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false, unique = true)
    var id: Long? = null,

    @Column(nullable = false)
    var season: Int,

    @Column(unique = true)
    var code: String? = null,

    var name: String? = null,

    var message : String ?=null,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var type: TeamType,

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "meetingTeam", cascade = [CascadeType.ALL], orphanRemoval = true)
    var information: Information? = null,

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "maleTeam")
    var maleMatch: Match? = null,

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "femaleTeam")
    var femaleMatch: Match? = null,

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "team", cascade = [CascadeType.ALL], orphanRemoval = true)
    var userTeams: MutableList<UserTeam> = mutableListOf(),

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "meetingTeam", cascade = [CascadeType.ALL], orphanRemoval = true)
    var preference: Preference? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    var payment: Payment?= null,
): BaseEntity()
