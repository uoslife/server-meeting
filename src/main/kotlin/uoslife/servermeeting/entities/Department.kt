package uoslife.servermeeting.entities

import jakarta.persistence.*

@Entity
@Table(name = "departments")
data class Department(

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    val id: Int,

    val name: String,

    val number: Number,

    @OneToOne(mappedBy = "department", fetch = FetchType.LAZY)
    val user: User? = null,

    @OneToMany(mappedBy = "department", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val noPreferDepartment: MutableList<NoPreferDepartment> = mutableListOf(),
)
