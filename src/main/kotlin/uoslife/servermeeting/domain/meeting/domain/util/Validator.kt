package uoslife.servermeeting.domain.meeting.domain.util

import org.springframework.stereotype.Component
import uoslife.servermeeting.domain.meeting.domain.dao.UserTeamDao
import uoslife.servermeeting.domain.meeting.domain.entity.MeetingTeam
import uoslife.servermeeting.domain.meeting.domain.exception.*
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

    fun isTeamNameLeast2Character(name: String?) {
        if (name == null || name.length < 2) {
            throw TeamNameLeast2CharacterException()
        }
    }

    fun isTeamCodeValid(code: String) {
        if (code.length != 4 || !code.matches(Regex("[A-Z0-9]{4}"))) {
            throw TeamCodeInvalidFormatException()
        }
    }

    fun isTeamFull(team: MeetingTeam) {
        if (userTeamDao.countByTeam(team) >= 3) {
            throw TeamFullException()
        }
    }

    fun isUserSameGenderWithTeamLeader(user: User, teamLeaderUser: User) {
        if (user.gender != teamLeaderUser.gender) {
            throw TeamConsistOfSameGenderException()
        }
    }

    fun isUserInTeam(user: User, team: MeetingTeam) {
        if (userTeamDao.findByUserAndTeam(user, team) == null) {
            throw UserNotInTeamException()
        }
    }
}
