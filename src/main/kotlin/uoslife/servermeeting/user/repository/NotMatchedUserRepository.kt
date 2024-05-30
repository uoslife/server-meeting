package uoslife.servermeeting.user.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import uoslife.servermeeting.user.entity.NotMatchedUser

@Repository
interface NotMatchedUserRepository: JpaRepository<NotMatchedUser, Long> {
}
