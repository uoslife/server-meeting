package uoslife.servermeeting.meetingteam.entity

import com.vladmihalcea.hibernate.type.json.JsonType
import jakarta.persistence.*
import org.hibernate.annotations.Type
import uoslife.servermeeting.match.entity.Match
import uoslife.servermeeting.meetingteam.entity.enums.TeamType
import uoslife.servermeeting.user.entity.User

@Entity
@Table(name = "meeting_team")
class MeetingTeam(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false, unique = true)
    var id: Long? = null,
    var season: Int? = null,
    @Column(nullable = false) var code: String,
    var name: String? = null,
    @Type(JsonType::class) @Column(columnDefinition = "jsonb") var information: Information? = null,
    @Type(JsonType::class) @Column(columnDefinition = "jsonb") var preference: Preference? = null,
    var message: String? = null,
    @Type(JsonType::class)
    @Column(columnDefinition = "jsonb")
    var compatibility: Compatibility? = null,
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "maleTeam") var maleMatch: Match? = null,
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "femaleTeam") var femaleMatch: Match? = null,
    @OneToMany(mappedBy = "team") var users: MutableList<User> = mutableListOf(),
    @OneToOne
    @JoinColumn(
        name = "leader_id",
        referencedColumnName = "id",
        unique = true,
        foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT)
    )
    var leader: User? = null,
    @Column(nullable = false) @Enumerated(EnumType.STRING) var type: TeamType,
)
