package uoslife.servermeeting.user.entity

import com.vladmihalcea.hibernate.type.json.JsonType
import jakarta.persistence.*
import org.hibernate.annotations.Type
import uoslife.servermeeting.global.common.BaseEntity
import uoslife.servermeeting.meetingteam.entity.UserTeam
import uoslife.servermeeting.user.dto.request.UserUpdateRequest
import uoslife.servermeeting.user.dto.response.UserFindResponse
import uoslife.servermeeting.user.entity.enums.GenderType

@Entity
@Table(name = "meetingUser")
class User(
    @Id @Column(nullable = false, unique = true) var id: Long? = null,
    @Column(unique = true) var phoneNumber: String? = null,
    var name: String = "",
    @Column(unique = true) var kakaoTalkId: String = "",
    @Enumerated(EnumType.STRING) var gender: GenderType = GenderType.MALE,
    var department: String = "",
    var studentNumber: Int? = null,
    var height: Int? = null,
    var age: Int = 0,
    @Type(JsonType::class)
    @Column(columnDefinition = "jsonb")
    var userAdditionInformation: UserAdditionInformation = UserAdditionInformation(),
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    var userTeams: MutableList<UserTeam> = mutableListOf(),
) : BaseEntity() {
    companion object {
        fun create(userId: Long): User {
            val user: User =
                User(
                    id = userId,
                )
            return user
        }

        fun toResponse(user: User): UserFindResponse {
            val userFindResponse: UserFindResponse =
                UserFindResponse(
                    name = user.name,
                    genderType = user.gender,
                    phoneNumber = user.phoneNumber,
                    age = user.age,
                    kakaoTalkId = user.kakaoTalkId,
                    department = user.department,
                    height = user.height,
                    smoking = user.userAdditionInformation.smoking,
                    spiritAnimal = user.userAdditionInformation.spiritAnimal,
                    mbti = user.userAdditionInformation.mbti,
                    interest = user.userAdditionInformation.interest,
                )

            return userFindResponse
        }
    }
    fun update(requestDto: UserUpdateRequest, newUserAdditionInformation: UserAdditionInformation) {
        name = requestDto.name
        gender = requestDto.gender
        department = requestDto.department
        studentNumber = requestDto.studentNumber
        phoneNumber = requestDto.phoneNumber ?: phoneNumber
        kakaoTalkId = requestDto.kakaoTalkId
        age = requestDto.age
        height = requestDto.height
        userAdditionInformation = newUserAdditionInformation
    }
}
