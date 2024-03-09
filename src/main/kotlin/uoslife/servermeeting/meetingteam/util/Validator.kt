package uoslife.servermeeting.meetingteam.util

import org.springframework.stereotype.Component
import uoslife.servermeeting.meetingteam.entity.MeetingTeam
import uoslife.servermeeting.meetingteam.exception.*
import uoslife.servermeeting.user.entity.User

@Component
class Validator() {

    fun isUserAlreadyHaveTeam(user: User) {
        if (user.team != null) {
            throw UserAlreadyHaveTeamException()
        }
    }

    fun isTeamNameInvalid(name: String?) {
        if (name == null || name.length < 2 || name.length > 8) {
            throw InvalidTeamNameException()
        }
    }

    fun isTeamCodeValid(code: String) {
        if (code.length != 4 || !code.matches(Regex("[A-Z0-9]{4}"))) {
            throw TeamCodeInvalidFormatException()
        }
    }

    fun isTeamFull(team: MeetingTeam) {
        if (team.users.size >= 3) {
            throw TeamFullException()
        }
    }

    fun isUserSameGenderWithTeamLeader(user: User, teamLeaderUser: User) {
        if (user.userPersonalInformation.gender != teamLeaderUser.userPersonalInformation.gender) {
            throw TeamConsistOfSameGenderException()
        }
    }
}
