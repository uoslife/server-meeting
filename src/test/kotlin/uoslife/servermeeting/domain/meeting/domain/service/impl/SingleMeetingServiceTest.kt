package uoslife.servermeeting.domain.meeting.domain.service.impl

import java.util.UUID
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import uoslife.servermeeting.domain.meeting.domain.common.SingleMeetingTest
import uoslife.servermeeting.domain.meeting.domain.entity.Information
import uoslife.servermeeting.domain.meeting.domain.entity.MeetingTeam
import uoslife.servermeeting.domain.meeting.domain.entity.UserTeam
import uoslife.servermeeting.domain.meeting.domain.entity.enums.TeamType
import uoslife.servermeeting.domain.meeting.domain.exception.InSingleMeetingTeamNoJoinTeamException
import uoslife.servermeeting.domain.meeting.domain.exception.InSingleMeetingTeamOnlyOneUserException
import uoslife.servermeeting.domain.meeting.domain.exception.InformationNotFoundException
import uoslife.servermeeting.domain.meeting.domain.exception.UserAlreadyHaveTeamException
import uoslife.servermeeting.domain.meeting.domain.exception.UserTeamNotFoundException
import uoslife.servermeeting.domain.user.domain.entity.enums.DepartmentNameType
import uoslife.servermeeting.domain.user.domain.entity.enums.GenderType
import uoslife.servermeeting.domain.user.domain.exception.UserNotFoundException

class SingleMeetingServiceTest : SingleMeetingTest() {

    @Test
    fun `존재하지 않는 사용자에 대해서 팀 생성시 오류를 발생`() {
        // given
        val notExistUserId = UUID.randomUUID()

        // when & then
        assertThatThrownBy { singleMeetingService.createMeetingTeam(notExistUserId, "") }
            .isInstanceOf(UserNotFoundException::class.java)
    }

    @Test
    fun `이미 사용자가 팀을 보유 시에 팀 생성 시 오류 발생`() {
        // given
        val user = userRepository.findAll().first()
        singleMeetingService.createMeetingTeam(user.id!!, "")

        // when & then
        assertThatThrownBy { singleMeetingService.createMeetingTeam(user.id!!, "") }
            .isInstanceOf(UserAlreadyHaveTeamException::class.java)
    }

    @Test
    fun `정상적으로 팀을 생성할 수 있다`() {
        // given
        val user = userRepository.findAll().first()

        // when
        val code = singleMeetingService.createMeetingTeam(user.id!!, "")

        // then
        assertThat(code).isEqualTo("")
        val findByUser = userTeamDao.findByUser(user)
        assertThat(findByUser).isNotNull
    }

    @Test
    fun `1대1 미팅에서 Join Team 요청 시 오류를 발생`() {
        // given
        val user = userRepository.findAll().first()
        val code = singleMeetingService.createMeetingTeam(user.id!!, "")

        // when & then
        assertThatThrownBy { singleMeetingService.joinMeetingTeam(user.id!!, code!!, true) }
            .isInstanceOf(InSingleMeetingTeamNoJoinTeamException::class.java)
    }

    @Test
    fun `1대1 미팅에서 User List를 불러올 시에 오류를 발생`() {
        // given
        val user = userRepository.findAll().first()

        // when & then
        assertThatThrownBy { singleMeetingService.getMeetingTeamUserList(user.id!!, "") }
            .isInstanceOf(InSingleMeetingTeamOnlyOneUserException::class.java)
    }

    @Test
    fun `팀 선호 및 정보 입력 시에 없는 User라면 오류 발생`() {
        // given
        val notExistUserId = UUID.randomUUID()
        val information = Information()

        // when & then
        assertThatThrownBy {
                singleMeetingService.updateMeetingTeamInformation(
                    notExistUserId,
                    information,
                )
            }
            .isInstanceOf(UserNotFoundException::class.java)
    }

    @Test
    fun `User Team이 존재하지 않는 User에 대해 팀 정보를 입력 시에 오류 발생`() {
        // given
        val user = userRepository.findAll().first()
        val information = Information()

        // when & then
        assertThatThrownBy {
                singleMeetingService.updateMeetingTeamInformation(
                    user.id!!,
                    information,
                )
            }
            .isInstanceOf(UserTeamNotFoundException::class.java)
    }

    @Test
    fun `정상적으로 팀 정보를 입력할 수 있다`() {
        // given
        val user = userRepository.findAll().first()
        val meetingTeam =
            meetingTeamRepository.save(
                MeetingTeam(
                    code = "",
                    name = "",
                    season = season,
                ),
            )
        val newUserTeam = UserTeam.createUserTeam(meetingTeam, user, true, TeamType.SINGLE)
        userTeamDao.saveUserTeam(newUserTeam)

        val updatedInformation = Information()

        // when
        singleMeetingService.updateMeetingTeamInformation(
            user.id!!,
            updatedInformation,
        )

        // then
        val userTeam = userTeamDao.findByUserWithMeetingTeam(user, TeamType.SINGLE)
        val information = meetingTeam.information ?: throw InformationNotFoundException()
        assertThat(userTeam).isNotNull
    }

    @Test
    fun `팀 전체 정보 조회 시에 없는 유저일 경우 오류 발생`() {
        // given
        val notExistUserId = UUID.randomUUID()

        // when & then
        assertThatThrownBy { singleMeetingService.getMeetingTeamInformation(notExistUserId) }
            .isInstanceOf(UserNotFoundException::class.java)
    }

    @Test
    fun `User Team이 존재하지 않는 User에 대해 팀 정보를 조회 시 오류 발생`() {
        // given
        val user = userRepository.findAll().first()

        // when & then
        assertThatThrownBy { singleMeetingService.getMeetingTeamInformation(user.id!!) }
            .isInstanceOf(UserTeamNotFoundException::class.java)
    }

    @Test
    fun `Information이 존재하지 않는 경우 팀 정보를 조회 요청 시에 오류 발생`() {
        // given
        val user = userRepository.findAll().first()
        val meetingTeam =
            meetingTeamRepository.save(
                MeetingTeam(
                    code = "",
                    name = "",
                    season = season,
                ),
            )
        val newUserTeam = UserTeam.createUserTeam(meetingTeam, user, true, TeamType.SINGLE)
        userTeamDao.saveUserTeam(newUserTeam)
        val information = Information()

        // when
        singleMeetingService.updateMeetingTeamInformation(
            user.id!!,
            information,
        )
        meetingTeam.information = null

        // then
        assertThatThrownBy { singleMeetingService.getMeetingTeamInformation(user.id!!) }
            .isInstanceOf(InformationNotFoundException::class.java)
    }

    @Test
    fun `정상적으로 팀 및 신청 정보를 조회할 수 있다`() {
        // given
        val user = userRepository.findAll().first()
        val meetingTeam =
            meetingTeamRepository.save(
                MeetingTeam(
                    code = "",
                    name = "",
                    season = season,
                ),
            )
        val newUserTeam = UserTeam.createUserTeam(meetingTeam, user, true, TeamType.SINGLE)
        userTeamDao.saveUserTeam(newUserTeam)
        val updatedInformation = Information()

        // when
        singleMeetingService.updateMeetingTeamInformation(
            user.id!!,
            updatedInformation,
        )

        val information = singleMeetingService.getMeetingTeamInformation(user.id!!)

        // then
        assertThat(information).isNotNull
        assertThat(information.sex).isEqualTo(GenderType.FEMALE)
        assertThat(information.teamType).isEqualTo(TeamType.SINGLE)
        assertThat(information.teamUserList[0].smoking).isEqualTo(false)
        assertThat(information.teamUserList[0].age).isEqualTo(24)
        assertThat(information.teamUserList[0].department)
            .isEqualTo(DepartmentNameType.COMPUTER_SCIENCE)
        assertThat(information.teamUserList[0].kakaoTalkId).isEqualTo("kakao")
        assertThat(information.teamUserList.size).isEqualTo(1)
    }

    @Test
    fun `존재하지 않는 사용자가 신청 정보 삭제 요청 시에 오류 발생`() {
        // given
        val notExistUserId = UUID.randomUUID()

        // when & then
        assertThatThrownBy { singleMeetingService.deleteMeetingTeam(notExistUserId) }
            .isInstanceOf(UserNotFoundException::class.java)
    }

    @Test
    fun `팀이 존재하지 않는 사용자가 팀 삭제 요청 시에 오류 발생`() {
        // given
        val user = userRepository.findAll().first()

        // when & then
        assertThatThrownBy { singleMeetingService.deleteMeetingTeam(user.id!!) }
            .isInstanceOf(UserTeamNotFoundException::class.java)
    }

    @Test
    fun `정상적으로 삭제가 가능하다`() {
        // given
        val user = userRepository.findAll().first()
        val meetingTeam =
            meetingTeamRepository.save(
                MeetingTeam(
                    code = "",
                    name = "",
                    season = season,
                ),
            )
        val newUserTeam = UserTeam.createUserTeam(meetingTeam, user, true, TeamType.SINGLE)
        userTeamDao.saveUserTeam(newUserTeam)
        val updatedInformation = Information()
        singleMeetingService.updateMeetingTeamInformation(
            user.id!!,
            updatedInformation,
        )

        // when && then
        singleMeetingService.deleteMeetingTeam(user.id!!)
    }
}
