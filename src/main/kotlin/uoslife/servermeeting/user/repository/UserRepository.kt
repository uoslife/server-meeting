package uoslife.servermeeting.user.repository

import org.springframework.data.jpa.repository.JpaRepository
import uoslife.servermeeting.user.entity.User

interface UserRepository : JpaRepository<User, Long> {
    fun findByPhoneNumber(phoneNumber: String): User?

    fun existsByKakaoTalkId(kakaoTalkId: String): Boolean

    fun findByEmail(email: String): User?
}
