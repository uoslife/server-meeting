package uoslife.servermeeting.entities

import jakarta.persistence.*

@Entity
@Table(name = "meeting_team")
class MeetingTeam(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false, unique = true)
    val id: Long? = null,

    @Column(nullable = false)
    val season: Int,

    @Column(nullable = false)
    val code: String,

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "meetingTeam")
    val information: Information? = null,

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "maleTeam")
    val maleCompatibility: Compatibility? = null,

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "femaleTeam")
    val femaleCompatibility: Compatibility? = null,

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "maleTeam")
    val maleMatch: Match? = null,

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "femaleTeam")
    val femaleMatch: Match? = null,

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "team")
    val userTeams: List<UserTeam> = mutableListOf(),

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "meetingTeam")
    val preference: List<Preference> = mutableListOf(),
)
