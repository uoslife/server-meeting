package uoslife.servermeeting.user.entity

import jakarta.persistence.*
import uoslife.servermeeting.user.entity.Department
import uoslife.servermeeting.user.entity.User

@Entity
@Table(name = "no_prefer_departments")
class NoPreferDepartment(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false, updatable = false)
    val id: Long,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    val department: Department,

    )
