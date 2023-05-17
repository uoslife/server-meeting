package uoslife.servermeeting.domain.user.domain.entity

import jakarta.persistence.*
import uoslife.servermeeting.domain.match.domain.entity.Report
import uoslife.servermeeting.domain.meeting.domain.entity.UserTeam
import uoslife.servermeeting.domain.user.domain.entity.enums.GenderType
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

    var nickname: String?,

    var name: String?,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    var department: Department? = null,

    @OneToMany(mappedBy = "user")
    var noPreferDepartments: MutableList<NoPreferDepartment> = mutableListOf(),

    @OneToMany(mappedBy = "user")
    var userTeams: MutableList<UserTeam> = mutableListOf(),

    @OneToMany(mappedBy = "user")
    var reports: MutableList<Report> = mutableListOf(),
) : BaseEntity()
