package uoslife.servermeeting.match.entity

import jakarta.persistence.*

@Entity
@Table(name = "compatibility_priorities")
data class CompatibilityPriority (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @Column(name = "priority", nullable = false)
    val priority: String,

    @Column(name = "weight", nullable = false)
    val weight: Int,
){}