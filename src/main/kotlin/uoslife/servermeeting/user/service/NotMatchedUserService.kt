package uoslife.servermeeting.user.service

import org.springframework.stereotype.Service
import uoslife.servermeeting.user.entity.NotMatchedUser
import uoslife.servermeeting.user.repository.NotMatchedUserRepository

@Service
class NotMatchedUserService(
    private val notMatchedUserRepository: NotMatchedUserRepository,
) {

    fun findAll(): List<NotMatchedUser> {
        return notMatchedUserRepository.findAll()
    }
}
