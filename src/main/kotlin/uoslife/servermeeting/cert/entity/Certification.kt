package uoslife.servermeeting.cert.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import uoslife.servermeeting.cert.dto.University
import uoslife.servermeeting.global.common.BaseEntity

@Entity
@Table(name = "certication")
class Certification(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false, unique = true)
    val id: Long? = null,
    val email: String,
    val university: University,
    val code: String? = null, // 인증 코드
    var isVerified: Boolean = false, // 인증 됐는지 확인
) : BaseEntity()
