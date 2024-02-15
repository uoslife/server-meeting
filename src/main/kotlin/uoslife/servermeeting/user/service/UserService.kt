package uoslife.servermeeting.user.service

import java.util.UUID
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uoslife.servermeeting.user.dao.UserPutDao
import uoslife.servermeeting.user.dao.UserUpdateDao
import uoslife.servermeeting.user.dto.request.UserUpdateRequest
import uoslife.servermeeting.user.dto.response.NicknameCheckResponse
import uoslife.servermeeting.user.dto.response.UserFindResponseDto
import uoslife.servermeeting.user.dto.response.toResponse
import uoslife.servermeeting.user.entity.User
import uoslife.servermeeting.user.exception.ExistingUserNotFoundException
import uoslife.servermeeting.user.exception.UserNotFoundException
import uoslife.servermeeting.user.repository.UserRepository
import uoslife.servermeeting.user.util.Validator

@Service
@Transactional(readOnly = true)
class UserService(
    private val userRepository: UserRepository,
    private val userUpdateDao: UserUpdateDao,
    private val userPutDao: UserPutDao,
    private val validator: Validator,
) {

    fun findUser(id: UUID): ResponseEntity<UserFindResponseDto> {
        val user = userRepository.findByIdOrNull(id) ?: throw UserNotFoundException()
        return ResponseEntity.ok(user.toResponse())
    }

    fun findUserByNickname(nickname: String): ResponseEntity<NicknameCheckResponse> {
        val user = userRepository.findUserByNickname(nickname)
        return ResponseEntity.ok(checkNicknameDuplication(user))
    }

    @Transactional
    fun updateUser(requestDto: UserUpdateRequest, id: UUID): ResponseEntity<Unit> {
        val existingUser =
            userRepository.findByIdOrNull(id) ?: throw ExistingUserNotFoundException()
        userUpdateDao.updateUser(requestDto, existingUser)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }

    @Transactional
    fun resetUser(id: UUID): ResponseEntity<Unit> {
        val user = userRepository.findByIdOrNull(id) ?: throw ExistingUserNotFoundException()
        validator.isUserDefault(user)
        return ResponseEntity.ok(userPutDao.putUser(user))
    }

    private fun checkNicknameDuplication(user: User?): NicknameCheckResponse {
        return if (user?.nickname == null) {
            NicknameCheckResponse(false)
        } else {
            NicknameCheckResponse(true)
        }
    }
}
