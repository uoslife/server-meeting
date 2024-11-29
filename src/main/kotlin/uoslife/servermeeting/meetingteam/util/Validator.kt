package uoslife.servermeeting.meetingteam.util

import org.springframework.stereotype.Component
import uoslife.servermeeting.meetingteam.entity.MeetingTeam
import uoslife.servermeeting.meetingteam.entity.enums.TeamType
import uoslife.servermeeting.meetingteam.exception.*
import uoslife.servermeeting.payment.entity.Payment
import uoslife.servermeeting.payment.entity.enums.PaymentStatus
import uoslife.servermeeting.user.entity.User

@Component
class Validator {
    fun isUserAlreadyHaveSingleTeam(user: User) {
        val userSingleTeam = user.userTeams.filter { it.team.type == TeamType.SINGLE }

        if (userSingleTeam.isNotEmpty()) {
            throw UserAlreadyHaveTeamException()
        }
    }

    fun isUserAlreadyHaveTripleTeam(user: User) {
        val userTripleTeam = user.userTeams.filter { it.team.type == TeamType.TRIPLE }

        if (userTripleTeam.isNotEmpty()) {
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
        if (team.userTeams.size >= 3) {
            throw TeamFullException()
        }
    }

    fun isMessageLengthIsValid(message: String?) {
        if (message == null || message.length < 10) {
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
