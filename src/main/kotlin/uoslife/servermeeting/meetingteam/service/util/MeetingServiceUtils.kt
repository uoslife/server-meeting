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
        userList: List<User>,
        information: Information,
        preference: Preference,
        teamName: String?,
        message: String?
    ): MeetingTeamInformationGetResponse {
        return MeetingTeamInformationGetResponse(
            teamType = teamType,
            teamName = teamName,
            gender = gender,
            teamUserList =
                userList.map {
                    UserProfile(
                        name = it.name,
                        age = it.userPersonalInformation.age,
                        height = it.userPersonalInformation.height,
                        university = it.userPersonalInformation.university,
                        department = it.userPersonalInformation.department,
                        studentType = it.userPersonalInformation.studentType,
                        kakaoTalkId = it.kakaoTalkId,
                        smoking = it.userPersonalInformation.smoking,
                        religion = it.userPersonalInformation.religion,
                        drinkingMin = it.userPersonalInformation.drinkingMin,
                        drinkingMax = it.userPersonalInformation.drinkingMax,
                        spiritAnimal = it.userPersonalInformation.spiritAnimal,
                        mbti = it.userPersonalInformation.mbti,
                        interest = it.userPersonalInformation.interest
                    )
                },
            information = information,
            preference = preference,
            message = message
        )
    }
}
