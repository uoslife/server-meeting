package uoslife.servermeeting.cert.repository

import org.springframework.data.jpa.repository.JpaRepository
import uoslife.servermeeting.cert.entity.Cert

interface CertRepository: JpaRepository<Cert, Long>{
    fun existsByCode(code: String): Boolean
    fun existsByEmailAndCodeAndIsVerifiedNot(email: String, code: String): Boolean
    fun existsByEmailAndIsVerified(email: String): Boolean
    fun findByEmailAndCodeAndIsVerifiedNot(email: String, code: String): Cert?
}
