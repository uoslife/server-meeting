package uoslife.servermeeting.domain.user.domain.service

import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.data.repository.findByIdOrNull
import uoslife.servermeeting.domain.user.domain.common.UserServiceTest

class putUserTest : UserServiceTest() {

    @Test
    fun `정상적으로 User 데이터가 put 됐는지 확인한다`() {
        // given
        val userUUID = userRepository.findAll().first().id!!

        // when
        userService.resetUser(userUUID)
        entityManager.flush()
        entityManager.clear()
        val updatedUser = userRepository.findByIdOrNull(userUUID)!!

        // then
        assertThat(updatedUser.department).isNull()
        assertThat(updatedUser.studentType).isNull()
        assertThat(updatedUser.smoking).isNull()
        assertThat(updatedUser.spiritAnimal).isNull()
        assertThat(updatedUser.mbti).isNull()
        assertThat(updatedUser.interest).isNull()
        assertThat(updatedUser.nickname).isNotNull()
        assertThat(updatedUser.height).isEqualTo(0)
    }
}
