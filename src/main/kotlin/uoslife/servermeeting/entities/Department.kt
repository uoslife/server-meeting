package uoslife.servermeeting.entities

import jakarta.persistence.*

@Entity
@Table(name = "department")
class Department(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false, unique = true)
    val id: Long? = null,

    val name: String,

    val number: Int,

    @OneToOne(mappedBy = "department", fetch = FetchType.LAZY)
    val user: User? = null,
) : BaseEntity()
