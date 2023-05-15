package uoslife.servermeeting.entities

import jakarta.persistence.*

@Entity
@Table(name = "report")
class Report(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false, unique = true)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User? = null,

    @Column(nullable = false, length = 255)
    val reportCategory: String,

    @Column(nullable = false)
    val reportText: String,

    val adminResponse: String,
) : BaseEntity()
