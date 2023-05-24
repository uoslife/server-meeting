package uoslife.servermeeting.domain.user.domain.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import uoslife.servermeeting.domain.user.application.request.UserUpdateRequestDto
import uoslife.servermeeting.domain.user.application.response.UserFindResponseDto
import uoslife.servermeeting.domain.user.application.response.toResponse
import uoslife.servermeeting.domain.user.domain.entity.User
import uoslife.servermeeting.domain.user.domain.exception.UserNotFoundException
import uoslife.servermeeting.domain.user.domain.repository.UserRepository
import java.util.UUID

@Service
class UserService(
    private val userRepository: UserRepository) {

    fun findUser(id: UUID): ResponseEntity<UserFindResponseDto>? {
        val user = userRepository.findByIdOrNull(id)
        return ResponseEntity.ok(user?.toResponse())
    }

    fun updateUser(body: UserUpdateRequestDto, id: UUID): String {
        return "updateUser"
    }
}
