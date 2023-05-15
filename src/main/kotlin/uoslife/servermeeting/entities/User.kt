package uoslife.servermeeting.entities

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "`user`")
class User(

    // inject by origin db
    @Id
    @Column(nullable = false, unique = true)
    val id: UUID? = null,

    val birthYear: Int? = null,

    val gender: GenderType? = null,

    val phoneNumber: String,

    val profilePicture: String,

    val nickname: String,

    val name: String,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    val department: Department? = null,

    @OneToMany(mappedBy = "user")
    val noPreferDepartments: List<NoPreferDepartment> = mutableListOf(),

    @OneToMany(mappedBy = "user")
    val userTeams: List<UserTeam> = mutableListOf(),

    @OneToMany(mappedBy = "user")
    val reports: List<Report> = mutableListOf(),
) : BaseEntity()
