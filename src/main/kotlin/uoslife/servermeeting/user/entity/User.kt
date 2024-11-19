package uoslife.servermeeting.user.entity

import jakarta.persistence.*
import uoslife.servermeeting.global.common.BaseEntity
import uoslife.servermeeting.meetingteam.entity.UserTeam
import uoslife.servermeeting.payment.entity.Payment
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
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "user", cascade = [CascadeType.REMOVE])
    var userInformation: UserInformation? = null,
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    var payments: MutableList<Payment>? = null,
) : BaseEntity() {
    companion object {
        fun create(email: String): User {
            return User(email = email)
        }
    }
    fun update(requestDto: UserUpdateRequest) {
        name = requestDto.name ?: name
        phoneNumber = requestDto.phoneNumber ?: phoneNumber
        kakaoTalkId = requestDto.kakaoTalkId ?: kakaoTalkId
        if (requestDto.genderType != null && gender != null) {
            throw GenderNotUpdatableException()
        }
        gender = requestDto.genderType
        studentType = requestDto.studentType
    }
}
