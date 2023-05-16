package uoslife.servermeeting.entities

import jakarta.persistence.*

@Entity
@Table(name = "meeting_team")
class MeetingTeam(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false, unique = true)
    var id: Long? = null,

    @Column(nullable = false)
    var season: Int,

    @Column(nullable = false)
    var code: String,

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "meetingTeam")
    var information: Information? = null,

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "maleTeam")
    var maleCompatibility: Compatibility? = null,

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "femaleTeam")
    var femaleCompatibility: Compatibility? = null,

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "maleTeam")
    var maleMatch: Match? = null,

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "femaleTeam")
    var femaleMatch: Match? = null,

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "team")
    var userTeams: MutableList<UserTeam> = mutableListOf(),

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "meetingTeam")
    var preference: MutableList<Preference> = mutableListOf(),
)
