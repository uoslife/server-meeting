package uoslife.servermeeting.certification.repository

import org.springframework.data.jpa.repository.JpaRepository
import uoslife.servermeeting.certification.entity.Certification

interface CertificationRepository : JpaRepository<Certification, Long> {
    fun existsByCode(code: String): Boolean
    fun existsByEmailAndCodeAndIsVerifiedNot(email: String, code: String): Boolean
    fun existsByEmailAndIsVerified(email: String): Boolean
    fun findByEmailAndCodeAndIsVerifiedNot(email: String, code: String): Certification?
}
