package uoslife.servermeeting.entities

import jakarta.persistence.*

@Entity
@Table(name = "compatibility_priorities")
data class CompatibilityPriority (

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    val id: Long,

    @Column(name = "priority", nullable = false)
    val priority: String,

    @Column(name = "weight", nullable = false)
    val weight: Int,
){}