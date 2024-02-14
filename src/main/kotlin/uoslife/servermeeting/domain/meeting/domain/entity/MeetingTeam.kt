package uoslife.servermeeting.domain.meeting.domain.entity

import com.vladmihalcea.hibernate.type.json.JsonType
import jakarta.persistence.*
import org.hibernate.annotations.Type
import uoslife.servermeeting.domain.match.domain.entity.Match
import uoslife.servermeeting.domain.meeting.domain.entity.enums.TeamType

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
    @Type(JsonType::class)
    @Column(columnDefinition = "jsonb")
    var compatibility: Compatibility? = null,
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "maleTeam") var maleMatch: Match? = null,
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "femaleTeam") var femaleMatch: Match? = null,
    @Column(nullable = false) var isLeader: Boolean,
    @Column(nullable = false) @Enumerated(EnumType.STRING) var type: TeamType,
)
