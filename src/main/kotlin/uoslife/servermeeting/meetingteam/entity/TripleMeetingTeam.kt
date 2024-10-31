package uoslife.servermeeting.meetingteam.entity

import com.vladmihalcea.hibernate.type.json.JsonType
import jakarta.persistence.*
import org.hibernate.annotations.Type
import uoslife.servermeeting.global.common.BaseEntity
import uoslife.servermeeting.match.entity.Match
import uoslife.servermeeting.user.entity.User

@Entity
class TripleMeetingTeam(
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
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "tripleMaleTeam") var maleMatch: Match? = null,
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "tripleFemaleTeam") var femaleMatch: Match? = null,
    @OneToMany(mappedBy = "tripleTeam") var users: MutableList<User> = mutableListOf(),
    @OneToOne
    @JoinColumn(
        name = "leader_id",
        referencedColumnName = "id",
        unique = true,
        foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT)
    )
    var leader: User? = null,
    @OneToOne(fetch = FetchType.LAZY) @JoinColumn(name = "payment_id") var payment: Payment? = null
) : BaseEntity()
