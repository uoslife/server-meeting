package uoslife.servermeeting.meeting.entity

import jakarta.persistence.*
import uoslife.servermeeting.match.entity.Compatibility
import uoslife.servermeeting.match.entity.Match

@Entity
@Table(name = "meeting_teams")
class MeetingTeam(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(
        name = "team_id",
        nullable = false
    ) val teamId: Long,

    @Column(name = "season", nullable = false) val season: Int,

    @Column(name = "code", nullable = false) val code: String,

    @OneToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY, mappedBy = "team") val information: Information,

    @OneToOne(
        cascade = [CascadeType.ALL],
        fetch = FetchType.LAZY,
        mappedBy = "maleTeam"
    ) val maleCompatibility: Compatibility,

    @OneToOne(
        cascade = [CascadeType.ALL],
        fetch = FetchType.LAZY,
        mappedBy = "femaleTeam"
    ) val femaleCompatibility: Compatibility,

    @OneToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY, mappedBy = "maleTeam") val maleMatch: Match,

    @OneToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY, mappedBy = "femaleTeam") val femaleMatch: Match,

    @OneToMany(cascade = [CascadeType.ALL], fetch = FetchType.LAZY, mappedBy = "team") val userTeams: List<UserTeam>,

    @OneToMany(cascade = [CascadeType.ALL], fetch = FetchType.LAZY, mappedBy = "team") val preference: List<Preference>,

    )