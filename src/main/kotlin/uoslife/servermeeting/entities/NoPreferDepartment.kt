package uoslife.servermeeting.entities

import jakarta.persistence.*

@Entity
@Table(name = "no_prefer_department")
class NoPreferDepartment(
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User? = null,

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    val department: Department? = null,
)
