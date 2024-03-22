package uoslife.servermeeting.user.util

import org.springframework.stereotype.Component
import uoslife.servermeeting.user.entity.User
import uoslife.servermeeting.user.exception.UserAlreadyResetException

@Component(value = "userValidator")
class Validator {
    fun isUserDefault(user: User) {
        if (user.userPersonalInformation.kakaoTalkId == null) {
            throw UserAlreadyResetException()
        }
    }
}
