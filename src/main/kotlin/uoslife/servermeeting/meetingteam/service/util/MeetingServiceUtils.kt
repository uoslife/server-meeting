package uoslife.servermeeting.meetingteam.service.util

import java.time.LocalDate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uoslife.servermeeting.meetingteam.dto.response.MeetingTeamInformationGetResponse
import uoslife.servermeeting.meetingteam.dto.response.UserProfile
import uoslife.servermeeting.meetingteam.entity.Information
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
        teamName: String?,
    ): MeetingTeamInformationGetResponse {
        val currentYear: Int = LocalDate.now().year

        return MeetingTeamInformationGetResponse(
            teamType = teamType,
            teamName = teamName,
            sex = gender,
            teamUserList =
                userList.map {
                    UserProfile(
                        nickname = it.nickname,
                        age = currentYear - it.userPersonalInformation?.birthYear!! + 1,
                        kakaoTalkId = it.userPersonalInformation?.kakaoTalkId!!,
                        department = it.userPersonalInformation?.department!!,
                        studentType = it.userPersonalInformation?.studentType!!,
                        height = it.userPersonalInformation?.height,
                        smoking = it.userPersonalInformation?.smoking,
                        spiritAnimal = it.userPersonalInformation?.spiritAnimal,
                        MBTI = it.userPersonalInformation?.mbti!!,
                        interest = it.userPersonalInformation?.interest,
                    )
                },
        )
    }
}
