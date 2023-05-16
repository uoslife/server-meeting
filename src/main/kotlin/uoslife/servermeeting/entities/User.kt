package uoslife.servermeeting.entities

import jakarta.persistence.*
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
    var gender: GenderType? = null,

    var phoneNumber: String,

    var profilePicture: String,

    var nickname: String,

    var name: String,

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
