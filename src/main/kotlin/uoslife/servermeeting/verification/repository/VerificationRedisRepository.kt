package uoslife.servermeeting.verification.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import uoslife.servermeeting.verification.entity.Verification

@Repository
interface VerificationRedisRepository : CrudRepository<Verification, String> {
    fun existsByEmail(email: String): Boolean
    fun findByEmailAndIsAndVerifiedNot(email: String): Verification?
    fun findByEmailAndCodeOrNull(email: String, code: String): Verification?
    fun existsByEmailAndCode(email: String, code: String): Boolean
    fun existsByEmailAndIsVerified(email: String): Boolean
    fun findByEmailOrNull(email: String): Verification?
    fun findByEmail(email: String): Verification
}
