package uoslife.servermeeting.verification.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import uoslife.servermeeting.verification.entity.Verification

@Repository
interface VerificationRedisRepository : CrudRepository<Verification, String> {
}
