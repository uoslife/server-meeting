package uoslife.servermeeting.meetingteam.util

import org.springframework.stereotype.Component
import uoslife.servermeeting.meetingteam.entity.MeetingTeam
import uoslife.servermeeting.meetingteam.entity.Payment
import uoslife.servermeeting.meetingteam.entity.enums.PaymentStatus
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

    fun isMessageLengthIsValid(message: String) {
        if (message.length < 10) {
            throw InvalidMessageLengthException()
        }
    }

    fun isAlreadyPaid(payment: Payment): Boolean {
        if (payment.status.equals(PaymentStatus.SUCCESS)) return true
        return false
    }

    fun setValidMBTI(mbti: String?): String? {
        if (mbti == null) return null
        // EI, SN, TF, JP
        var validMBTI = ""
        val mbtiList = listOf("E", "I", "S", "N", "T", "F", "J", "P")

        for (value in mbtiList) {
            if (mbti.contains(value)) {
                validMBTI += value
            }
        }
        return validMBTI
    }
}
