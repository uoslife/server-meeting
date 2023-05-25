package uoslife.servermeeting.domain.user.domain.service

import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import uoslife.servermeeting.domain.user.domain.common.UserServiceTest

class findUserByNicknameTest : UserServiceTest() {

    @Test
    fun `존재하는 nickname에 대한 중복 체크`() {
        // given
        val parameter = "nickname1"

        // when
        val response = userService.findUserByNickname("nickname1")

        // then
        assertThat(response).isNotNull
        assertThat(response.body?.duplication).isEqualTo(true)
    }

    @Test
    fun `존재하지 않는 nickname에 대한 중복 체크`() {
        // given
        val parameter = "nickname0"

        // when
        val response = userService.findUserByNickname("nickname0")

        // then
        assertThat(response).isNotNull
        assertThat(response.body?.duplication).isEqualTo(false)
    }
}
