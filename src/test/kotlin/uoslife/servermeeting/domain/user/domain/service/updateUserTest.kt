package uoslife.servermeeting.domain.user.domain.service

import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.data.repository.findByIdOrNull
import uoslife.servermeeting.domain.user.application.request.UserUpdateRequest
import uoslife.servermeeting.domain.user.domain.common.UserServiceTest
import uoslife.servermeeting.domain.user.domain.entity.enums.DepartmentNameType
import uoslife.servermeeting.domain.user.domain.entity.enums.GenderType
import uoslife.servermeeting.domain.user.domain.entity.enums.StudentType

class updateUserTest : UserServiceTest() {

    @Test
    fun `정상적으로 User 데이터가 update 됐는지 확인한다`() {
        // given
        val userUUID = userRepository.findAll().first().id!!
        val updateData = UserUpdateRequest(
            2000, GenderType.MALE, DepartmentNameType.BUSINESS, "kakao", StudentType.GRADUATE,
            false, "chicken", "ENTJ", "coding", 173, "nickname0",
        )

        // when
        userService.updateUser(updateData, userUUID)
        entityManager.flush()
        entityManager.clear()

        val updatedUser = userRepository.findByIdOrNull(userUUID)!!

        // then
        assertThat(updatedUser.gender).isEqualTo(updateData.gender)
        assertThat(updatedUser.department).isEqualTo(updateData.department)
        assertThat(updatedUser.kakaoTalkId).isEqualTo(updateData.kakaoTalkId)
        assertThat(updatedUser.studentType).isEqualTo(updateData.studentType)
        assertThat(updatedUser.smoking).isEqualTo(updateData.smoking)
        assertThat(updatedUser.spiritAnimal).isEqualTo(updateData.spiritAnimal)
        assertThat(updatedUser.mbti).isEqualTo(updateData.mbti)
        assertThat(updatedUser.interest).isEqualTo(updateData.interest)
        assertThat(updatedUser.height).isEqualTo(updateData.height)
        assertThat(updatedUser.nickname).isEqualTo(updateData.nickname)
    }
}
