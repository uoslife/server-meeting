package uoslife.servermeeting.domain.user.domain.entity

import jakarta.persistence.*
import uoslife.servermeeting.domain.user.domain.entity.enums.DepartmentNameType

@Entity
@Table(name = "no_prefer_department")
class NoPreferDepartment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false, unique = true)
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    var user: User? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var department: DepartmentNameType,
)
