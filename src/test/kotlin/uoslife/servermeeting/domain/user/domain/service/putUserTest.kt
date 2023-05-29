package uoslife.servermeeting.domain.user.domain.service

import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.data.repository.findByIdOrNull
import uoslife.servermeeting.domain.user.domain.common.UserServiceTest
import uoslife.servermeeting.domain.user.domain.exception.ExistingUserNotFoundException
import java.util.UUID

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

    @Test
    fun `user 데이터의 uuid가 일치하는 지 확인한다`() {
        // given
        val userUUID = userRepository.findAll().first().id!!

        // when
        userService.resetUser(userUUID)
        entityManager.flush()
        entityManager.clear()
        val updatedUser = userRepository.findByIdOrNull(userUUID)!!

        // then
        assertThat(updatedUser.id).isEqualTo(userUUID)
    }

    @Test
    fun `존재하지 않는 uuid에 대한 요청에 에러를 반환한다`() {
        // given
        val userUUID = UUID.randomUUID()

        // then & when
        assertThatThrownBy { userService.resetUser(userUUID) }
            .isInstanceOf(ExistingUserNotFoundException::class.java)
    }
}
