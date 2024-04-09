package uoslife.servermeeting.user.entity

import com.vladmihalcea.hibernate.type.json.JsonType
import jakarta.persistence.*
import java.util.*
import org.hibernate.annotations.Type
import uoslife.servermeeting.global.common.BaseEntity
import uoslife.servermeeting.meetingteam.entity.MeetingTeam
import uoslife.servermeeting.meetingteam.entity.Payment
import uoslife.servermeeting.verification.dto.University

@Entity
@Table(name = "`user`")
class User(
    @Id @Column(nullable = false, unique = true) var id: UUID? = null,
    var phoneNumber: String = "",
    var name: String = "",
    @Column(nullable = true, unique = true) val email: String? = null,
    var kakaoTalkId: String = "",
    @Type(JsonType::class)
    @Column(columnDefinition = "jsonb")
    var userPersonalInformation: UserPersonalInformation = UserPersonalInformation(),
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "user") var payment: Payment? = null,
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "team_id") var team: MeetingTeam? = null
) : BaseEntity() {
    companion object {
        fun create(email: String, university: University): User {
            val user: User =
                User(
                    id = UUID.randomUUID(),
                    email = email,
                )
            user.userPersonalInformation.university = university
            return user
        }
    }
}
