package uoslife.servermeeting.user.entity

import jakarta.persistence.*
import uoslife.servermeeting.global.entity.BaseEntity
import uoslife.servermeeting.meeting.entity.UserTeam
import uoslife.servermeeting.payment.entity.Payment
import java.util.UUID

@Entity
@Table(name = "users")
class User(

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @OneToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    val department: Department? = null,

    val age: Int? = null,

    val gender: String? = null,

    val phoneNumber: String? = null,

    val profilePicture: String? = null,

    val nickname: String? = null,

    val name: String? = null,

    val authId: UUID? = null,

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val noPreferDepartments: MutableList<NoPreferDepartment> = mutableListOf(),

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val userTeam: MutableList<UserTeam> = mutableListOf(),

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val report: MutableList<Report> = mutableListOf(),

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val payment: MutableList<Payment> = mutableListOf(),





    ) : BaseEntity() {}
