package uoslife.servermeeting.domain.user.domain.entity

import com.vladmihalcea.hibernate.type.json.JsonType
import jakarta.persistence.*
import java.util.*
import org.hibernate.annotations.Type
import uoslife.servermeeting.domain.meeting.domain.entity.MeetingTeam
import uoslife.servermeeting.domain.meeting.domain.entity.Payment
import uoslife.servermeeting.domain.meeting.domain.entity.UserTeam
import uoslife.servermeeting.global.common.BaseEntity

@Entity
@Table(name = "`user`")
class User(
    @Id @Column(nullable = false, unique = true) var id: UUID? = null,
    var phoneNumber: String?,
    @Column(nullable = false, unique = true) var nickname: String,
    var name: String?,
    var email: String? = null,
    var kakaoTalkId: String,
    @Type(JsonType::class)
    @Column(columnDefinition = "jsonb")
    var userPersonalInformation: UserPersonalInformation = UserPersonalInformation(),
    @OneToMany(mappedBy = "user") var userTeams: MutableList<UserTeam> = mutableListOf(),
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "user") var payment: Payment? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    var team: MeetingTeam? = null
) : BaseEntity()
