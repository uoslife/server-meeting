package uoslife.servermeeting.domain.user.domain.util

import org.springframework.stereotype.Component
import uoslife.servermeeting.domain.user.domain.entity.User
import uoslife.servermeeting.domain.user.domain.exception.UserAlreadyResetException

@Component(value = "userValidator")
class Validator {
    fun isUserDefault(user: User) {
        if (user.kakaoTalkId == null) {
            throw UserAlreadyResetException()
        }
    }
}
