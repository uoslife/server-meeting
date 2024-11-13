package uoslife.servermeeting.user.entity

import com.vladmihalcea.hibernate.type.json.JsonType
import jakarta.persistence.*
import org.hibernate.annotations.Type
import uoslife.servermeeting.global.common.BaseEntity
import uoslife.servermeeting.meetingteam.entity.Payment
import uoslife.servermeeting.meetingteam.entity.SingleMeetingTeam
import uoslife.servermeeting.meetingteam.entity.TripleMeetingTeam
import uoslife.servermeeting.user.dto.Interest
import uoslife.servermeeting.user.dto.request.CreateProfileRequest
import uoslife.servermeeting.user.dto.request.UserUpdateRequest
import uoslife.servermeeting.user.dto.response.UserFindResponse
import uoslife.servermeeting.user.entity.enums.GenderType
import uoslife.servermeeting.user.entity.enums.StudentType

@Entity
@Table(name = "meetingUser")
class User(
    @Id @Column(nullable = false, unique = true) var id: Long? = null,
    @Column var isProfileCompleted: Boolean = false,
    @Column(unique = true) var email: String? = null,
    @Column(unique = true) var phoneNumber: String? = null,
    var name: String = "",
    @Column(unique = true) var kakaoTalkId: String = "",
    @Enumerated(EnumType.STRING) var gender: GenderType = GenderType.MALE,
    @Enumerated(EnumType.STRING) var studentStatus: StudentType = StudentType.UNDERGRADUATE,
    var department: String = "",
    var studentNumber: Int? = null,
    @ElementCollection
    @CollectionTable(name = "user_interests", joinColumns = [JoinColumn(name = "user_id")])
    @Column(name = "interest_name")
    var interests: MutableList<Interest> = mutableListOf(),
    var height: Int? = null,
    var age: Int = 0,
    @Type(JsonType::class)
    @Column(columnDefinition = "jsonb")
    var userAdditionInformation: UserAdditionInformation = UserAdditionInformation(),
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    var payments: MutableList<Payment>? = mutableListOf(),
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "triple_team_id", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    var tripleTeam: TripleMeetingTeam? = null,
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "single_team_id", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    var singleTeam: SingleMeetingTeam? = null,
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
                    email = user.email,
                    genderType = user.gender,
                    phoneNumber = user.phoneNumber,
                    age = user.age,
                    kakaoTalkId = user.kakaoTalkId,
                    department = user.department,
                    height = user.height,
                    smoking = user.userAdditionInformation.smoking,
                    mbti = user.userAdditionInformation.mbti,
                    interests = user.interests,
                    tripleTeam = user.tripleTeam != null,
                    singleTeam = user.singleTeam != null
                )

            return userFindResponse
        }
    }
    fun update(requestDto: UserUpdateRequest, newUserAdditionInformation: UserAdditionInformation) {
        name = requestDto.name
        gender = requestDto.gender
        studentStatus = requestDto.studentStatus
        department = requestDto.department
        studentNumber = requestDto.studentNumber
        interests = requestDto.interests ?: mutableListOf()
        phoneNumber = requestDto.phoneNumber ?: phoneNumber
        kakaoTalkId = requestDto.kakaoTalkId
        age = requestDto.age
        height = requestDto.height
        userAdditionInformation = newUserAdditionInformation
    }

    fun createProfile(requestDto: CreateProfileRequest) {
        name = requestDto.name
        isProfileCompleted = true
        gender = requestDto.gender
        age = requestDto.age
        phoneNumber = requestDto.phoneNumber ?: phoneNumber
        kakaoTalkId = requestDto.kakaoTalkId
        studentStatus = requestDto.studentStatus
        department = requestDto.department
        studentNumber = requestDto.studentNumber
        interests = requestDto.interests ?: mutableListOf()
    }
}
