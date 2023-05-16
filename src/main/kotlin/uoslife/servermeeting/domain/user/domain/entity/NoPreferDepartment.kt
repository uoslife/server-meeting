package uoslife.servermeeting.domain.user.domain.entity

import jakarta.persistence.*

@Entity
@Table(name = "no_prefer_department")
class NoPreferDepartment(
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    var user: User? = null,

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    var department: Department? = null,
)
