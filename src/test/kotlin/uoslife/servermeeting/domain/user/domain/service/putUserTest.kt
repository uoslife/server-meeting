package uoslife.servermeeting.domain.user.domain.service

import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.data.repository.findByIdOrNull
import uoslife.servermeeting.domain.user.application.request.UserUpdateRequest
import uoslife.servermeeting.domain.user.domain.common.UserServiceTest
import uoslife.servermeeting.domain.user.domain.entity.enums.DepartmentNameType
import uoslife.servermeeting.domain.user.domain.entity.enums.GenderType
import uoslife.servermeeting.domain.user.domain.entity.enums.StudentType
import uoslife.servermeeting.domain.user.domain.exception.ExistingUserNotFoundException
import uoslife.servermeeting.domain.user.domain.exception.UserAlreadyResetException
import java.util.UUID

class putUserTest : UserServiceTest() {

    @Test
    fun `정상적으로 User 데이터가 put 됐는지 확인한다`() {
        // given
        val user = userRepository.findAll().first()
        val updateData = UserUpdateRequest(
            2000, GenderType.MALE, DepartmentNameType.BUSINESS, "kakao", StudentType.GRADUATE,
            false, "chicken", "ENTJ", "coding", 173, "nickname0",
        )
        userUpdateDao.updateUser(updateData, user)
        entityManager.flush()
        entityManager.clear()

        // when
        userService.resetUser(user.id!!)
        entityManager.flush()
        entityManager.clear()
        val updatedUser = userRepository.findByIdOrNull(user.id)!!

        // then
        assertThat(updatedUser.department).isNull()
        assertThat(updatedUser.kakaoTalkId).isNull()
        assertThat(updatedUser.studentType).isNull()
        assertThat(updatedUser.smoking).isNull()
        assertThat(updatedUser.spiritAnimal).isNull()
        assertThat(updatedUser.mbti).isNull()
        assertThat(updatedUser.interest).isNull()
        assertThat(updatedUser.nickname).isNotNull()
        assertThat(updatedUser.height).isEqualTo(0)
    }

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
