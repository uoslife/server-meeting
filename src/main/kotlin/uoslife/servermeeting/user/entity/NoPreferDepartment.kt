package uoslife.servermeeting.user.entity

import jakarta.persistence.*
import uoslife.servermeeting.user.entity.Department
import uoslife.servermeeting.user.entity.User

@Entity
@Table(name = "no_prefer_departments")
class NoPreferDepartment(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    val department: Department,

    )
