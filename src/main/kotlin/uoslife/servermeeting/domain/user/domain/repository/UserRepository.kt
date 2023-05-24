package uoslife.servermeeting.domain.user.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import uoslife.servermeeting.domain.user.domain.entity.User
import java.util.*

interface UserRepository : JpaRepository<User, UUID> {
    fun findUserById(id: UUID): User
}
