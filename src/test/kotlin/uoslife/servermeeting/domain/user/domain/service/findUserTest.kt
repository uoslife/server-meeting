package uoslife.servermeeting.domain.user.domain.service

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import uoslife.servermeeting.domain.user.domain.common.UserServiceTest

class findUserTest : UserServiceTest() {
    @Test
    fun `User의 정보 조회`() {
        // given
        val user = userRepository.findAll().first()

        // when
        val response = userService.findUser(user.id!!)

        // then
        Assertions.assertThat(response?.body?.id).isEqualTo(user.id)
    }
}
