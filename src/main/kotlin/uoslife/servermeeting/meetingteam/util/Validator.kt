package uoslife.servermeeting.meetingteam.util

import org.springframework.stereotype.Component
import uoslife.servermeeting.meetingteam.dao.UserTeamDao
import uoslife.servermeeting.meetingteam.entity.MeetingTeam
import uoslife.servermeeting.meetingteam.exception.TeamCodeInvalidFormatException
import uoslife.servermeeting.meetingteam.exception.TeamConsistOfSameGenderException
import uoslife.servermeeting.meetingteam.exception.TeamFullException
import uoslife.servermeeting.meetingteam.exception.TeamNameLeast2CharacterException
import uoslife.servermeeting.meetingteam.exception.UserAlreadyHaveTeamException
import uoslife.servermeeting.meetingteam.exception.UserNotInTeamException
import uoslife.servermeeting.user.entity.User

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
        if (user.userPersonalInformation.gender != teamLeaderUser.userPersonalInformation.gender) {
            throw TeamConsistOfSameGenderException()
        }
    }

    fun isUserInTeam(user: User, team: MeetingTeam) {
        if (userTeamDao.findByUserAndTeam(user, team) == null) {
            throw UserNotInTeamException()
        }
    }
}
