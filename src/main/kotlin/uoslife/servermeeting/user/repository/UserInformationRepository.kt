package uoslife.servermeeting.user.repository

import org.springframework.data.jpa.repository.JpaRepository
import uoslife.servermeeting.user.entity.User
import uoslife.servermeeting.user.entity.UserInformation

interface UserInformationRepository : JpaRepository<UserInformation, Long> {
    fun findByUser(user: User): UserInformation?
}
