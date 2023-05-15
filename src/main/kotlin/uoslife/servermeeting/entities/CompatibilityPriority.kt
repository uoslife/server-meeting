package uoslife.servermeeting.entities

import jakarta.persistence.*

@Entity
@Table(name = "compatibility_priority")
class CompatibilityPriority(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false, unique = true)
    val id: Long? = null,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    val weight: Int,
)
