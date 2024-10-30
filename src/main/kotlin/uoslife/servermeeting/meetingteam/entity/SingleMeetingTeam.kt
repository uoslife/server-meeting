package uoslife.servermeeting.meetingteam.entity

import com.vladmihalcea.hibernate.type.json.JsonType
import jakarta.persistence.*
import org.hibernate.annotations.Type
import uoslife.servermeeting.global.common.BaseEntity
import uoslife.servermeeting.match.entity.Match
import uoslife.servermeeting.user.entity.User

@Entity
class SingleMeetingTeam(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false, unique = true)
    var id: Long? = null,
    var season: Int? = null,
    @Type(JsonType::class) @Column(columnDefinition = "jsonb") var information: Information? = null,
    @Type(JsonType::class) @Column(columnDefinition = "jsonb") var preference: Preference? = null,
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "maleTeam") var maleMatch: Match? = null,
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "femaleTeam") var femaleMatch: Match? = null,
    @OneToOne
    @JoinColumn(
        name = "leader_id",
        referencedColumnName = "id",
        unique = true,
        foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT)
    )
    var leader: User? = null,
) : BaseEntity()
