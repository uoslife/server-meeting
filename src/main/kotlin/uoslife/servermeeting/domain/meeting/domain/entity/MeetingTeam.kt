package uoslife.servermeeting.domain.meeting.domain.entity

import com.vladmihalcea.hibernate.type.json.JsonType
import jakarta.persistence.*
import org.hibernate.annotations.Type
import uoslife.servermeeting.domain.match.domain.entity.Compatibility
import uoslife.servermeeting.domain.match.domain.entity.Match

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

    var name: String? = null,

    @Type(JsonType::class)
    @Column(columnDefinition = "jsonb")
    var information: Information? = null,

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "maleTeam")
    var maleCompatibility: Compatibility? = null,

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "femaleTeam")
    var femaleCompatibility: Compatibility? = null,

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "maleTeam")
    var maleMatch: Match? = null,

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "femaleTeam")
    var femaleMatch: Match? = null,

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "team", cascade = [CascadeType.ALL], orphanRemoval = true)
    var userTeams: MutableList<UserTeam> = mutableListOf(),

    @Type(JsonType::class)
    @Column(columnDefinition = "jsonb")
    var preference: Preference? = null,
)
