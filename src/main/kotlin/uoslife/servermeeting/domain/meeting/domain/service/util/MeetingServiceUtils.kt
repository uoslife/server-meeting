package uoslife.servermeeting.domain.meeting.domain.service.util

import java.time.LocalDate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uoslife.servermeeting.domain.meeting.application.response.MeetingTeamInformationGetResponse
import uoslife.servermeeting.domain.meeting.application.response.UserProfile
import uoslife.servermeeting.domain.meeting.domain.entity.Information
import uoslife.servermeeting.domain.meeting.domain.entity.enums.TeamType
import uoslife.servermeeting.domain.user.domain.entity.User
import uoslife.servermeeting.domain.user.domain.entity.enums.GenderType

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
