package uoslife.servermeeting.domain.user.domain.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uoslife.servermeeting.domain.user.application.request.UserUpdateRequest
import uoslife.servermeeting.domain.user.application.response.NicknameCheckResponse
import uoslife.servermeeting.domain.user.application.response.UserFindResponseDto
import uoslife.servermeeting.domain.user.application.response.toResponse
import uoslife.servermeeting.domain.user.domain.dao.UserPutDao
import uoslife.servermeeting.domain.user.domain.dao.UserUpdateDao
import uoslife.servermeeting.domain.user.domain.entity.User
import uoslife.servermeeting.domain.user.domain.exception.ExistingUserNotFoundException
import uoslife.servermeeting.domain.user.domain.exception.UserNotFoundException
import uoslife.servermeeting.domain.user.domain.repository.UserRepository
import uoslife.servermeeting.domain.user.domain.util.UserValidator
import java.util.*

@Service
@Transactional(readOnly = true)
class UserService(
    private val userRepository: UserRepository,
    private val userUpdateDao: UserUpdateDao,
    private val userPutDao: UserPutDao,
    private val userValidator: UserValidator,
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
        val existingUser = userRepository.findByIdOrNull(id) ?: throw ExistingUserNotFoundException()
        userUpdateDao.updateUser(requestDto, existingUser)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }

    @Transactional
    fun resetUser(id: UUID): ResponseEntity<Unit> {
        val user = userRepository.findByIdOrNull(id) ?: throw ExistingUserNotFoundException()
        userValidator.alreadyResetUser(user)
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
