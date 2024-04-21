package uoslife.servermeeting.user.entity

import com.vladmihalcea.hibernate.type.json.JsonType
import jakarta.persistence.*
import java.util.*
import org.hibernate.annotations.Type
import uoslife.servermeeting.global.common.BaseEntity
import uoslife.servermeeting.meetingteam.entity.MeetingTeam
import uoslife.servermeeting.meetingteam.entity.Payment
import uoslife.servermeeting.user.dto.response.UserFindResponse
import uoslife.servermeeting.verification.dto.University

@Entity
@Table(name = "`user`")
class User(
    @Id @Column(nullable = false, unique = true) var id: UUID? = null,
    @Column(unique = true) var phoneNumber: String? = null,
    var name: String = "",
    @Column(nullable = true, unique = true) val email: String? = null,
    var kakaoTalkId: String = "",
    @Type(JsonType::class)
    @Column(columnDefinition = "jsonb")
    var userPersonalInformation: UserPersonalInformation = UserPersonalInformation(),
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "user") var payment: Payment? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    var team: MeetingTeam? = null
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

        fun toResponse(user: User): UserFindResponse {
            val userFindResponse: UserFindResponse =
                UserFindResponse(
                    name = user.name,
                    genderType = user.userPersonalInformation.gender,
                    phoneNumber = user.phoneNumber,
                    age = user.userPersonalInformation.age,
                    kakaoTalkId = user.kakaoTalkId,
                    department = user.userPersonalInformation.department,
                    studentType = user.userPersonalInformation.studentType,
                    height = user.userPersonalInformation.height,
                    religion = user.userPersonalInformation.religion,
                    drinkingMin = user.userPersonalInformation.drinkingMin,
                    drinkingMax = user.userPersonalInformation.drinkingMax,
                    smoking = user.userPersonalInformation.smoking,
                    spiritAnimal = user.userPersonalInformation.spiritAnimal,
                    mbti = user.userPersonalInformation.mbti,
                    interest = user.userPersonalInformation.interest,
                    university = user.userPersonalInformation.university,
                )

            return userFindResponse
        }
    }
}
