package uoslife.servermeeting.match.entity

import jakarta.persistence.*

@Entity
@Table(name = "compatibility_priority")
class CompatibilityPriority(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false, unique = true)
    var id: Long? = null,
    @Column(nullable = false) var name: String,
    @Column(nullable = false) var weight: Int,
)
