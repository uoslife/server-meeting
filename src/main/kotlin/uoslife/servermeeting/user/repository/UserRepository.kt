package uoslife.servermeeting.user.repository

import java.util.UUID
import org.springframework.data.jpa.repository.JpaRepository
import uoslife.servermeeting.user.entity.User

interface UserRepository : JpaRepository<User, UUID> {
    fun findByKakaoTalkId(kakaoId: String): User?
}
