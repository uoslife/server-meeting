package uoslife.servermeeting.domain.meeting.domain.service.impl

import java.util.UUID
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import uoslife.servermeeting.domain.meeting.domain.common.TripleMeetingTest
import uoslife.servermeeting.meetingteam.entity.Information
import uoslife.servermeeting.meetingteam.entity.enums.TeamType
import uoslife.servermeeting.meetingteam.exception.MeetingTeamNotFoundException
import uoslife.servermeeting.meetingteam.exception.TeamCodeInvalidFormatException
import uoslife.servermeeting.meetingteam.exception.TeamConsistOfSameGenderException
import uoslife.servermeeting.meetingteam.exception.TeamFullException
import uoslife.servermeeting.meetingteam.exception.TeamNameLeast2CharacterException
import uoslife.servermeeting.meetingteam.exception.UserAlreadyHaveTeamException
import uoslife.servermeeting.meetingteam.exception.UserNotInTeamException
import uoslife.servermeeting.meetingteam.exception.UserTeamNotFoundException
import uoslife.servermeeting.user.entity.enums.GenderType
import uoslife.servermeeting.user.exception.UserNotFoundException

class TripleMeetingServiceTest : TripleMeetingTest() {

    @Test
    fun `존재하지 않는 사용자에 대해서 팀 생성시 오류를 발생`() {
        // given
        val notExistUserId = UUID.randomUUID()

        // when & then
        Assertions.assertThatThrownBy { tripleMeetingService.createMeetingTeam(notExistUserId, "") }
            .isInstanceOf(UserNotFoundException::class.java)
    }

    @Test
    fun `이미 사용자가 팀을 보유 시에 팀 생성 시 오류 발생`() {
        // given
        val user = userRepository.findAll().first()
        tripleMeetingService.createMeetingTeam(user.id!!, "abcd")

        // when & then
        Assertions.assertThatThrownBy { tripleMeetingService.createMeetingTeam(user.id!!, "abab") }
            .isInstanceOf(UserAlreadyHaveTeamException::class.java)
    }

    @Test
    fun `팀 이름이 2글자 미만인 경우 오류 발생`() {
        // given
        val user = userRepository.findAll().first()

        // when & then
        Assertions.assertThatThrownBy { tripleMeetingService.createMeetingTeam(user.id!!, "") }
            .isInstanceOf(TeamNameLeast2CharacterException::class.java)
    }

    @Test
    fun `정상적으로 팀을 생성할 수 있다`() {
        // given
        val user = userRepository.findAll().first()

        // when
        val code = tripleMeetingService.createMeetingTeam(user.id!!, "abcd")

        // then
        val meetingTeam = meetingTeamRepository.findByCode(code!!) ?: MeetingTeamNotFoundException()
        assertThat(meetingTeam).isNotNull
    }

    @Test
    fun `존재하지 않는 사용자에 대해서 팀 참가시 오류를 발생`() {
        // given
        val notExistUserId = UUID.randomUUID()

        // when & then
        Assertions.assertThatThrownBy {
                tripleMeetingService.joinMeetingTeam(notExistUserId, "", true)
            }
            .isInstanceOf(UserNotFoundException::class.java)
    }

    @Test
    fun `정상적이지 않은 팀 코드에 대해 참가 요청시 오류 발생`() {
        // given
        val user = userRepository.findAll().first()

        // when & then
        Assertions.assertThatThrownBy { tripleMeetingService.joinMeetingTeam(user.id!!, "", true) }
            .isInstanceOf(TeamCodeInvalidFormatException::class.java)

        Assertions.assertThatThrownBy {
                tripleMeetingService.joinMeetingTeam(user.id!!, "zzzz", true)
            }
            .isInstanceOf(TeamCodeInvalidFormatException::class.java)
    }

    @Test
    fun `이미 사용자가 팀을 보유 시에 팀 참가 시 오류 발생`() {
        // given
        val userList = userRepository.findAll()
        val user1 = userList[0]
        val user2 = userList[1]

        val code1 = tripleMeetingService.createMeetingTeam(user1.id!!, "abcd")
        tripleMeetingService.createMeetingTeam(user2.id!!, "efgh")

        // when & then
        Assertions.assertThatThrownBy {
                tripleMeetingService.joinMeetingTeam(user2.id!!, code1!!, true)
            }
            .isInstanceOf(UserAlreadyHaveTeamException::class.java)
    }

    @Test
    fun `꽉 찬 팀에 대해서 참가 요청 시 오류를 발생`() {
        // given
        val userList = userRepository.findAll()
        val user1 = userList[0]
        val user2 = userList[1]
        val user3 = userList[2]
        val user4 = userList[3]

        val code1 = tripleMeetingService.createMeetingTeam(user1.id!!, "abcd")
        tripleMeetingService.joinMeetingTeam(user2.id!!, code1!!, true)
        tripleMeetingService.joinMeetingTeam(user3.id!!, code1, true)

        // when & then
        Assertions.assertThatThrownBy {
                tripleMeetingService.joinMeetingTeam(user4.id!!, code1, true)
            }
            .isInstanceOf(TeamFullException::class.java)
    }

    @Test
    fun `리더와 다른 성별의 참가자가 참가 신청 시 오류를 발생`() {
        // given
        val userList = userRepository.findAll()
        val maleUser =
            userList.find { it -> it.userPersonalInformation.gender == GenderType.MALE }
                ?: throw UserNotFoundException()
        val femaleUser =
            userList.find { it -> it.userPersonalInformation.gender == GenderType.FEMALE }
                ?: throw UserNotFoundException()

        val code1 = tripleMeetingService.createMeetingTeam(maleUser.id!!, "abcd")

        // when & then
        Assertions.assertThatThrownBy {
                tripleMeetingService.joinMeetingTeam(femaleUser.id!!, code1!!, true)
            }
            .isInstanceOf(TeamConsistOfSameGenderException::class.java)
    }

    @Test
    fun `팀에 참가하기 전에 정상적으로 팀의 정보를 받을 수 있다`() {
        // given
        val userList = userRepository.findAll()
        val user1 = userList[0]
        val user2 = userList[1]
        val user3 = userList[2]

        userList.map { it -> it.userPersonalInformation.gender = GenderType.FEMALE }

        // when
        val code1 = tripleMeetingService.createMeetingTeam(user1.id!!, "abcd")
        val joinMeetingTeam = tripleMeetingService.joinMeetingTeam(user2.id!!, code1!!, true)
        val meetingTeamUserListGetResponse =
            tripleMeetingService.joinMeetingTeam(user3.id!!, code1, false)

        // then
        assertThat(joinMeetingTeam).isNull()
        assertThat(meetingTeamUserListGetResponse).isNotNull
        assertThat(meetingTeamUserListGetResponse?.teamName).isEqualTo("abcd")
        assertThat(meetingTeamUserListGetResponse?.userList?.size).isEqualTo(2)
    }

    @Test
    fun `정상적으로 팀에 참가할 수 있다`() {
        // given
        val userList = userRepository.findAll()
        val user1 = userList[0]
        val user2 = userList[1]
        val user3 = userList[2]

        userList.map { it -> it.userPersonalInformation.gender = GenderType.FEMALE }

        // when & then
        val code1 = tripleMeetingService.createMeetingTeam(user1.id!!, "abcd")
        tripleMeetingService.joinMeetingTeam(user2.id!!, code1!!, true)
        tripleMeetingService.joinMeetingTeam(user3.id!!, code1, true)
    }

    @Test
    fun `존재하지 않는 사용자에 대해 팀 유저 리스트 조회시 오류 발생`() {
        // given
        val notExistUserId = UUID.randomUUID()

        // when & then
        Assertions.assertThatThrownBy {
                tripleMeetingService.getMeetingTeamUserList(notExistUserId, "")
            }
            .isInstanceOf(UserNotFoundException::class.java)
    }

    @Test
    fun `정상적이지 않은 팀 코드에 대해 유저 리스트 조회 시 오류를 발생`() {
        // given
        val user = userRepository.findAll().first()

        // when & then
        Assertions.assertThatThrownBy { tripleMeetingService.getMeetingTeamUserList(user.id!!, "") }
            .isInstanceOf(TeamCodeInvalidFormatException::class.java)
    }

    @Test
    fun `존재하지 않는 팀에 대한 팀 코드에 대해 유저 리스트 조회 시 오류를 발생`() {
        // given
        val user = userRepository.findAll().first()

        // when & then
        Assertions.assertThatThrownBy {
                tripleMeetingService.getMeetingTeamUserList(user.id!!, "AB19")
            }
            .isInstanceOf(MeetingTeamNotFoundException::class.java)
    }

    @Test
    fun `본인이 속하지 않은 팀에 대해서 유저 리스트 조회 시 오류를 발생`() {
        // given
        val userList = userRepository.findAll()
        val user1 = userList[0]
        val user2 = userList[1]
        val user3 = userList[2]
        val user4 = userList[3]

        // when
        val code1 = tripleMeetingService.createMeetingTeam(user1.id!!, "abcd")
        tripleMeetingService.joinMeetingTeam(user2.id!!, code1!!, true)
        tripleMeetingService.joinMeetingTeam(user3.id!!, code1, true)

        // then
        Assertions.assertThatThrownBy {
                tripleMeetingService.getMeetingTeamUserList(user4.id!!, code1)
            }
            .isInstanceOf(UserNotInTeamException::class.java)
    }

    @Test
    fun `정상적으로 유저 리스트를 조회할 수 있다`() {
        // given
        val userList = userRepository.findAll()
        val user1 = userList[0]
        val user2 = userList[1]
        val user3 = userList[2]

        // when
        val code1 = tripleMeetingService.createMeetingTeam(user1.id!!, "abcd")
        tripleMeetingService.joinMeetingTeam(user2.id!!, code1!!, true)
        tripleMeetingService.joinMeetingTeam(user3.id!!, code1, true)

        // then
        val teamUserList = tripleMeetingService.getMeetingTeamUserList(user1.id!!, code1)
        val teamAgeList = teamUserList.userList.map { it -> it.age }
        teamAgeList.sortedBy { it }

        assertThat(teamUserList.userList.size).isEqualTo(3)
        assertThat(teamUserList.teamName).isEqualTo("abcd")
        assertThat(teamAgeList[1]!! - teamAgeList[0]!!).isLessThan(3)
    }

    @Test
    fun `존재하지 않는 사용자에 대해 팀 정보 기입시 오류 발생`() {
        // given
        val notExistUserId = UUID.randomUUID()
        val questions: Map<String, String> =
            mapOf(
                "love" to "0001",
                "hate" to "0001",
            )
        val information = Information("0001", "0001", "male", questions)
        // when & then
        Assertions.assertThatThrownBy {
                tripleMeetingService.updateMeetingTeamInformation(notExistUserId, information)
            }
            .isInstanceOf(UserNotFoundException::class.java)
    }

    @Test
    fun `팀에 속하지 않은 사용자가 팀 정보 기입시 오류 발생`() {
        // given
        val user = userRepository.findAll().first()
        val questions: Map<String, String> =
            mapOf(
                "love" to "0001",
                "hate" to "0001",
            )
        val information = Information("0001", "0001", "male", questions)
        // when & then
        Assertions.assertThatThrownBy {
                tripleMeetingService.updateMeetingTeamInformation(user.id!!, information)
            }
            .isInstanceOf(UserTeamNotFoundException::class.java)
    }

    @Test
    fun `정상적으로 팀 정보를 기입하고 조회할 수 있다`() {
        // given
        val userList = userRepository.findAll()
        val user1 = userList[0]
        val user2 = userList[1]
        val user3 = userList[2]

        userList.map { it -> it.userPersonalInformation.gender = GenderType.FEMALE }

        // when
        val code1 = tripleMeetingService.createMeetingTeam(user1.id!!, "abcd")
        tripleMeetingService.joinMeetingTeam(user2.id!!, code1!!, true)
        tripleMeetingService.joinMeetingTeam(user3.id!!, code1, true)
        val questions: Map<String, String> =
            mapOf(
                "love" to "0001",
                "hate" to "0001",
            )
        val information = Information("0001", "0001", "male", questions)
        tripleMeetingService.updateMeetingTeamInformation(user1.id!!, information)

        // then
        val meetingInfo = tripleMeetingService.getMeetingTeamInformation(user1.id!!)
        assertThat(meetingInfo.sex).isEqualTo(GenderType.FEMALE)
        assertThat(meetingInfo.teamType).isEqualTo(TeamType.TRIPLE)
    }

    @Test
    fun `존재하지 않는 사용자에 대해 팀 정보 조회 시 오류 발생`() {
        // given
        val notExistUserId = UUID.randomUUID()

        // when & then
        Assertions.assertThatThrownBy {
                tripleMeetingService.getMeetingTeamInformation(notExistUserId)
            }
            .isInstanceOf(UserNotFoundException::class.java)
    }

    @Test
    fun `팀에 속하지 않은 사용자가 팀 정보 조회 시 오류 발생`() {
        // given
        val user = userRepository.findAll().first()

        // when & then
        Assertions.assertThatThrownBy { tripleMeetingService.getMeetingTeamInformation(user.id!!) }
            .isInstanceOf(UserTeamNotFoundException::class.java)
    }

    @Test
    fun `존재하지 않는 사용자가 팀 삭제를 요청 시 오류 발생`() {
        // given
        val notExistUserId = UUID.randomUUID()

        // when & then
        Assertions.assertThatThrownBy { tripleMeetingService.deleteMeetingTeam(notExistUserId) }
            .isInstanceOf(UserNotFoundException::class.java)
    }

    @Test
    fun `팀에 속하지 않은 사용자가 팀 삭제를 요청 시 오류 발생`() {
        // given
        val user = userRepository.findAll().first()

        // when & then
        Assertions.assertThatThrownBy { tripleMeetingService.deleteMeetingTeam(user.id!!) }
            .isInstanceOf(UserTeamNotFoundException::class.java)
    }

    @Test
    fun `정상적으로 팀을 삭제할 수 있다`() {
        // given
        val userList = userRepository.findAll()
        val user1 = userList[0]
        val user2 = userList[1]
        val user3 = userList[2]

        userList.map { it -> it.userPersonalInformation.gender = GenderType.FEMALE }

        // when
        val code1 = tripleMeetingService.createMeetingTeam(user1.id!!, "abcd")
        tripleMeetingService.joinMeetingTeam(user2.id!!, code1!!, true)
        tripleMeetingService.joinMeetingTeam(user3.id!!, code1, true)
        val questions: Map<String, String> =
            mapOf(
                "love" to "0001",
                "hate" to "0001",
            )
        val information = Information("0001", "0001", "male", questions)

        tripleMeetingService.updateMeetingTeamInformation(user1.id!!, information)

        // then
        tripleMeetingService.deleteMeetingTeam(user1.id!!)
    }
}
