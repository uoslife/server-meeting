package uoslife.servermeeting.user.repository

import java.util.UUID
import org.springframework.data.jpa.repository.JpaRepository
import uoslife.servermeeting.user.entity.User

interface UserRepository : JpaRepository<User, UUID> {
    fun existsByEmail(email: String): Boolean
    fun findByEmail(email: String): User?
    fun existsByPhoneNumber(phoneNumber: String): Boolean
    fun findByPhoneNumber(phoneNumber: String): User?
}
