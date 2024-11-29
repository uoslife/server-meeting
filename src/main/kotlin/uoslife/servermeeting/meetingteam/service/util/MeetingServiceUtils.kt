package uoslife.servermeeting.meetingteam.service.util

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uoslife.servermeeting.meetingteam.dto.response.MeetingTeamInformationGetResponse
import uoslife.servermeeting.meetingteam.dto.response.PreferenceDto
import uoslife.servermeeting.meetingteam.dto.response.UserCardProfile
import uoslife.servermeeting.meetingteam.entity.Preference
import uoslife.servermeeting.meetingteam.entity.UserTeam
import uoslife.servermeeting.meetingteam.entity.enums.TeamType
import uoslife.servermeeting.meetingteam.exception.UserInfoNotCompletedException
import uoslife.servermeeting.user.entity.enums.GenderType

@Service
@Transactional(readOnly = true)
class MeetingServiceUtils {

    fun toMeetingTeamInformationGetResponse(
        gender: GenderType,
        teamType: TeamType,
        userTeams: List<UserTeam>,
        preference: Preference,
        teamName: String?,
        course: String?
    ): MeetingTeamInformationGetResponse {
        return MeetingTeamInformationGetResponse(
            teamType = teamType,
            teamName = teamName,
            gender = gender,
            meetingTeamUserProfiles = userTeams.map(this::toUserCardProfile),
            preference = PreferenceDto.valueOf(preference),
            course = course
        )
    }

    private fun toUserCardProfile(userTeam: UserTeam): UserCardProfile {
        val userInfo = userTeam.user.userInformation ?: throw UserInfoNotCompletedException()
        try {
            return UserCardProfile(
                isLeader = userTeam.isLeader,
                name = userTeam.user.name!!,
                studentType = userInfo.studentType!!,
                department = userInfo.department!!,
                studentNumber = userInfo.studentNumber,
                age = userInfo.age!!,
                height = userInfo.height,
                mbti = userInfo.mbti,
                appearanceType = userInfo.appearanceType,
                eyelidType = userInfo.eyelidType,
                smoking = userInfo.smoking,
                interest = userInfo.interest,
                kakaoTalkId = userTeam.user.kakaoTalkId!!
            )
        } catch (e: NullPointerException) {
            throw UserInfoNotCompletedException()
        }
    }
}
