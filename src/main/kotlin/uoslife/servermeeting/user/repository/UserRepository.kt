package uoslife.servermeeting.user.repository

import org.springframework.data.jpa.repository.JpaRepository
import uoslife.servermeeting.user.entity.User

interface UserRepository : JpaRepository<User, Long> {

}
