package uoslife.servermeeting.entities

import jakarta.persistence.*

@Entity
@Table(name = "department")
class Department(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false, unique = true)
    var id: Long? = null,

    var name: String,

    var number: Int,

    @OneToOne(mappedBy = "department", fetch = FetchType.LAZY)
    var user: User? = null,
) : BaseEntity()
