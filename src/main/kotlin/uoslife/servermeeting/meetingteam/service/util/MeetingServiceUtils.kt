package uoslife.servermeeting.meetingteam.service.util

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uoslife.servermeeting.meetingteam.dto.response.MeetingTeamInformationGetResponse
import uoslife.servermeeting.meetingteam.dto.response.UserProfile
import uoslife.servermeeting.meetingteam.entity.Information
import uoslife.servermeeting.meetingteam.entity.Preference
import uoslife.servermeeting.meetingteam.entity.enums.TeamType
import uoslife.servermeeting.user.entity.User
import uoslife.servermeeting.user.entity.enums.GenderType

@Service
@Transactional(readOnly = true)
class MeetingServiceUtils {

    fun toMeetingTeamInformationGetResponse(
        gender: GenderType,
        teamType: TeamType,
        user: User,
        information: Information,
        preference: Preference,
        teamName: String?,
        message: String?
    ): MeetingTeamInformationGetResponse {
        return MeetingTeamInformationGetResponse(
            teamType = teamType,
            teamName = teamName,
            gender = gender,
            opponentLeaderProfile =
                UserProfile(
                    name = user.name,
                    age = user.userInformation?.age,
                    department = user.userInformation?.department,
                    kakaoTalkId = user.kakaoTalkId,
                    smoking = user.userInformation?.smoking,
                    mbti = user.userInformation?.mbti,
                    interest = user.userInformation?.interest,
                    height = user.userInformation?.height,
                    phoneNumber = user.phoneNumber,
                ),
            information = information,
            preference = preference,
            message = message
        )
    }
}
