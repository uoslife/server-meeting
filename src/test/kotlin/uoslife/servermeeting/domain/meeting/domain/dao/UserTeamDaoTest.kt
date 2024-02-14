package uoslife.servermeeting.domain.meeting.domain.dao

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import uoslife.servermeeting.domain.meeting.domain.common.UserTeamTest
import uoslife.servermeeting.domain.meeting.domain.entity.UserTeam
import uoslife.servermeeting.meetingteam.entity.enums.TeamType
import uoslife.servermeeting.meetingteam.exception.UserTeamNotFoundException

class UserTeamDaoTest : UserTeamTest() {

    @Test
    fun `정상적으로 UserTeam Entity를 저장하고 조회한다`() {
        // given
        val user = userRepository.findAll().first()
        val meetingTeam = meetingTeamRepository.findAll().first()

        // when
        val newUserTeam = UserTeam.createUserTeam(meetingTeam, user, true, TeamType.SINGLE)
        userTeamDao.saveUserTeam(newUserTeam)
        entityManager.flush()
        entityManager.clear()

        val findByUser = userTeamDao.findByUser(user) ?: throw UserTeamNotFoundException()

        // then
        assertThat(findByUser.isLeader).isEqualTo(true)
        assertThat(findByUser.type).isEqualTo(TeamType.SINGLE)
        assertThat(findByUser.team.name).isEqualTo("name")
        assertThat(findByUser.user?.name).isEqualTo("name")
        assertThat(findByUser.team.season).isEqualTo(3)
    }
}
