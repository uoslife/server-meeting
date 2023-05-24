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

    fun updateUser(requestDto: UserUpdateRequestDto, id: UUID): ResponseEntity<UUID> {
        val existingUser = userRepository.findByIdOrNull(id)

        existingUser?.let {
            it.birthYear = requestDto.birthYear
            it.gender = requestDto.gender!!
            it.name = requestDto.name
            it.department = requestDto.department
            it.studentType = requestDto.studentType
            it.smoking = requestDto.smoking
            it.spiritAnimal = requestDto.spirit_animal
            it.mbti = requestDto.mbti
            it.interest = requestDto.interest
            it.height = requestDto.height!!
            it.nickname = requestDto.nickname!!

            userRepository.save(it)
        }

        return ResponseEntity.ok(id);
    }
}
