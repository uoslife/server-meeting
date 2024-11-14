package uoslife.servermeeting.user.entity

import jakarta.persistence.*
import uoslife.servermeeting.global.common.BaseEntity
import uoslife.servermeeting.meetingteam.entity.UserTeam
import uoslife.servermeeting.user.dto.request.CreateProfileRequest
import uoslife.servermeeting.user.dto.request.UserUpdateRequest
import uoslife.servermeeting.user.entity.enums.GenderType
import uoslife.servermeeting.user.entity.enums.StudentType
import uoslife.servermeeting.user.exception.GenderNotUpdatableException

@Entity
@Table(name = "meetingUser")
class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long? = null,
    @Column(unique = true) var phoneNumber: String? = null,
    var name: String? = null,
    @Column(unique = true) var kakaoTalkId: String? = null,
    @Enumerated(EnumType.STRING) var gender: GenderType? = null,
    @Column(unique = true) var email: String? = null,
    @Enumerated(EnumType.STRING) var studentType: StudentType? = null,
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    var userTeams: MutableList<UserTeam> = mutableListOf(),
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "user")
    var userInformation: UserInformation? = null,
) : BaseEntity() {
    companion object {
        fun create(userId: Long): User {
            val user: User =
                User(
                    id = userId,
                )
            return user
        }

        fun create(email: String): User {
            return User(email = email)
        }

        //        fun toResponse(user: User): UserFindResponse {
        //            val userFindResponse: UserFindResponse =
        //                UserFindResponse(
        //                    name = user.name,
        //                    genderType = user.gender,
        //                    phoneNumber = user.phoneNumber,
        //                    age = user.userInformation?.age,
        //                    kakaoTalkId = user.kakaoTalkId,
        //                    department = user.userInformation?.department,
        //                    height = user.userInformation?.height,
        //                    smoking = user.userInformation?.smoking,
        //                    mbti = user.userInformation?.mbti,
        //                    interest = user.userInformation?.interest,
        //                )
        //
        //            return userFindResponse
        //        }
    }
    fun update(requestDto: UserUpdateRequest) {
        name = requestDto.name ?: name
        phoneNumber = requestDto.phoneNumber ?: phoneNumber
        kakaoTalkId = requestDto.kakaoTalkId ?: kakaoTalkId
        if (requestDto.genderType != null && gender != null) {
            throw GenderNotUpdatableException()
        }
        gender = requestDto.genderType
    }

    fun createProfile(requestDto: CreateProfileRequest) {
        name = requestDto.name
        gender = requestDto.gender
        phoneNumber = requestDto.phoneNumber ?: phoneNumber
        kakaoTalkId = requestDto.kakaoTalkId
        studentType = requestDto.studentType
        userInformation?.department = requestDto.department
        userInformation?.studentNumber = requestDto.studentNumber
        userInformation?.age = requestDto.age
        userInformation?.interest = requestDto.interest
    }
}
