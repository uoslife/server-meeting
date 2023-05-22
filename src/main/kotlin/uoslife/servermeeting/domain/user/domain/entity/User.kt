package uoslife.servermeeting.domain.user.domain.entity

import jakarta.persistence.*
import uoslife.servermeeting.domain.meeting.domain.entity.UserTeam
import uoslife.servermeeting.domain.user.domain.entity.enums.*
import uoslife.servermeeting.global.common.BaseEntity
import java.util.*

@Entity
@Table(name = "`user`")
class User(

    // inject by origin db
    @Id
    @Column(nullable = false, unique = true)
    var id: UUID? = null,

    var birthYear: Int? = null,

    @Enumerated(EnumType.STRING)
    var gender: GenderType = GenderType.MALE,

    var phoneNumber: String?,

    var profilePicture: String?,

    @Column(nullable = false, unique = true)
    var nickname: String,

    var name: String?,

    @Column(name = "height")
    var height: Int = 0,

    var kakaoTalkId: String? = null,

    @Enumerated(EnumType.STRING)
    var studentType: StudentType? = null,

    @Enumerated(EnumType.STRING)
    var department: DepartmentNameType? = null,

    var studentNumber: String? = null,

    var smoking: Boolean? = null,

    var spiritAnimal: String? = null,

    var mbti: String? = null,

    var interest: String? = null,

    @OneToMany(mappedBy = "user")
    var noPreferDepartments: MutableList<NoPreferDepartment> = mutableListOf(),

    @OneToMany(mappedBy = "user")
    var userTeams: MutableList<UserTeam> = mutableListOf(),
) : BaseEntity()
