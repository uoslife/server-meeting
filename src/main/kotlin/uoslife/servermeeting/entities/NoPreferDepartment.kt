package uoslife.servermeeting.entities

import jakarta.persistence.*

@Entity
@Table(name = "no_prefer_departments")
data class NoPreferDepartment(

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
