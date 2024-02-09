package uoslife.servermeeting.domain.user.domain.service

import java.util.UUID
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import uoslife.servermeeting.domain.user.domain.common.UserServiceTest
import uoslife.servermeeting.domain.user.domain.exception.ExistingUserNotFoundException
import uoslife.servermeeting.domain.user.domain.exception.UserAlreadyResetException

class putUserTest : UserServiceTest() {

  @Test
  fun `이미 reset된 user에 대한 reset 요청에 에러를 반환한다`() {
    // given
    val userUUID = userRepository.findAll().first().id!!

    // when & then
    assertThatThrownBy { userService.resetUser(userUUID) }
        .isInstanceOf(UserAlreadyResetException::class.java)
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
