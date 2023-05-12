package uoslife.servermeeting.user.entity

import jakarta.persistence.*
import uoslife.servermeeting.global.entity.BaseEntity

@Entity
@Table(name = "reports")
class Report(
    @Id
    @Column(name = "report_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val reportId: Long,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User,

    @Column(name = "report_category", nullable = false)
    val reportCategory: String,

    @Column(name = "report_text", nullable = false)
    val reportText: String,

    @Column(name = "admin_response", nullable = true)
    val adminResponse: String? = null,

    ) : BaseEntity() {}
