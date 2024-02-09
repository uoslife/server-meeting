package uoslife.servermeeting.domain.user.domain.entity

import com.vladmihalcea.hibernate.type.json.JsonType
import jakarta.persistence.*
import java.util.*
import org.hibernate.annotations.Type
import uoslife.servermeeting.domain.meeting.domain.entity.UserTeam
import uoslife.servermeeting.global.common.BaseEntity

@Entity
@Table(name = "`user`")
class User(
    @Id @Column(nullable = false, unique = true) var id: UUID? = null,
    var phoneNumber: String?,
    var profilePicture: String?,
    @Column(nullable = false, unique = true) var nickname: String,
    var name: String?,
    var email: String? = null,
    var emailVerified: Boolean = false,
    @Type(JsonType::class)
    @Column(columnDefinition = "jsonb")
    var userPersonalInformation: UserPersonalInformation = UserPersonalInformation(),
    @OneToMany(mappedBy = "user") var userTeams: MutableList<UserTeam> = mutableListOf(),
) : BaseEntity()
