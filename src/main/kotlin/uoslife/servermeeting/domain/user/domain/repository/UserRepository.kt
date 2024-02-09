package uoslife.servermeeting.domain.user.domain.repository

import java.util.*
import org.springframework.data.jpa.repository.JpaRepository
import uoslife.servermeeting.domain.user.domain.entity.User

interface UserRepository : JpaRepository<User, UUID> {
  fun findUserByNickname(nickname: String): User?
}
