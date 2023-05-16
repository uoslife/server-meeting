package uoslife.servermeeting.domain.user.domain.entity

import jakarta.persistence.*
import uoslife.servermeeting.domain.user.domain.entity.enums.DepartmentNameType
import uoslife.servermeeting.global.common.BaseEntity

@Entity
@Table(name = "department")
class Department(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false, unique = true)
    var id: Long? = null,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var name: DepartmentNameType,

    @Column(nullable = false)
    var number: Int,

    @OneToOne(mappedBy = "department", fetch = FetchType.LAZY)
    var user: User? = null,
) : BaseEntity()