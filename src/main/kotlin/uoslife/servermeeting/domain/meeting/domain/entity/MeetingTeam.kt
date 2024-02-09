package uoslife.servermeeting.domain.meeting.domain.entity

import com.vladmihalcea.hibernate.type.json.JsonType
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import org.hibernate.annotations.Type
import uoslife.servermeeting.domain.match.domain.entity.Match

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
    @OneToMany(
        fetch = FetchType.LAZY,
        mappedBy = "team",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    var userTeams: MutableList<UserTeam> = mutableListOf(),
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "meetingTeam") var payment: Payment? = null,
)
