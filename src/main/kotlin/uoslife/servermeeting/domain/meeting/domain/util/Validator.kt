package uoslife.servermeeting.domain.meeting.domain.util

import org.springframework.stereotype.Component
import uoslife.servermeeting.domain.meeting.domain.dao.UserTeamDao
import uoslife.servermeeting.domain.meeting.domain.exception.UserAlreadyHaveTeamException
import uoslife.servermeeting.domain.user.domain.entity.User


@Component
class Validator(
    private val userTeamDao: UserTeamDao,
) {

    fun isUserAlreadyHaveTeam(user: User) {
        if (userTeamDao.findByUser(user) != null) {
            throw UserAlreadyHaveTeamException()
        }
    }
}
