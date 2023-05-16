package uoslife.servermeeting.entities

import jakarta.persistence.*

@Entity
@Table(name = "report")
class Report(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false, unique = true)
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    var user: User? = null,

    var category: String,

    @Column(nullable = false, length = 255)
    var text: String,

    var adminResponse: String,
) : BaseEntity()
