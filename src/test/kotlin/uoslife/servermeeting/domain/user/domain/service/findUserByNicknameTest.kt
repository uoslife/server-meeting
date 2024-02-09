package uoslife.servermeeting.domain.user.domain.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import uoslife.servermeeting.domain.user.domain.common.UserServiceTest

class findUserByNicknameTest : UserServiceTest() {

    @Test
    fun `존재하는 nickname에 대한 중복 체크`() {
        // given
        val parameter = "nickname1"

        // when
        val response = userService.findUserByNickname(parameter)

        // then
        assertThat(response).isNotNull
        assertThat(response.body?.duplicated).isEqualTo(true)
    }

    @Test
    fun `존재하지 않는 nickname에 대한 중복 체크`() {
        // given
        val parameter = "nickname0"

        // when
        val response = userService.findUserByNickname(parameter)

        // then
        assertThat(response).isNotNull
        assertThat(response.body?.duplicated).isEqualTo(false)
    }
}
