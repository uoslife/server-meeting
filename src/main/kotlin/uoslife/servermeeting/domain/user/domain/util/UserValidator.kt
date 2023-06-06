package uoslife.servermeeting.domain.user.domain.util

import org.springframework.stereotype.Component
import uoslife.servermeeting.domain.user.domain.entity.User
import uoslife.servermeeting.domain.user.domain.exception.UserAlreadyResetException

@Component
class UserValidator {
    fun alreadyResetUser(user: User) {
        if (user.kakaoTalkId == null) {
            throw UserAlreadyResetException()
        }
    }
}
