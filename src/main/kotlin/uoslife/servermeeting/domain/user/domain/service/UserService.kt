package uoslife.servermeeting.domain.user.domain.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import uoslife.servermeeting.domain.user.application.request.UserUpdateRequest
import uoslife.servermeeting.domain.user.application.response.NicknameCheckResponse
import uoslife.servermeeting.domain.user.application.response.UserFindResponseDto
import uoslife.servermeeting.domain.user.application.response.toResponse
import uoslife.servermeeting.domain.user.domain.dao.UserUpdateDao
import uoslife.servermeeting.domain.user.domain.entity.User
import uoslife.servermeeting.domain.user.domain.exception.ExistingUserNotFoundException
import uoslife.servermeeting.domain.user.domain.exception.UserNotFoundException
import uoslife.servermeeting.domain.user.domain.repository.UserRepository
import java.util.UUID

@Service
class UserService(
    private val userRepository: UserRepository,
    private val userUpdateDao: UserUpdateDao) {

    fun findUser(id: UUID): ResponseEntity<UserFindResponseDto> {
        val user = userRepository.findByIdOrNull(id) ?: throw UserNotFoundException()
        return ResponseEntity.ok(user.toResponse())
    }

    fun updateUser(requestDto: UserUpdateRequest, id: UUID): ResponseEntity<Unit> {
        val existingUser = userRepository.findByIdOrNull(id) ?: throw ExistingUserNotFoundException()
        userUpdateDao.updateUser(requestDto, existingUser)
        return ResponseEntity.status(HttpStatus.OK).build()
    }

    fun findUserByNickname(nickname: String): ResponseEntity<NicknameCheckResponse> {
        val user = userRepository.findUserByNickname(nickname)
        return ResponseEntity.ok(checkNicknameDuplication(user))
    }

    private fun checkNicknameDuplication(user: User?): NicknameCheckResponse {
        return if (user?.nickname == null) NicknameCheckResponse(false)
        else NicknameCheckResponse(true)
    }
}
