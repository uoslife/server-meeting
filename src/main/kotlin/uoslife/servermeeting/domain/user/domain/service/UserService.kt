package uoslife.servermeeting.domain.user.domain.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import uoslife.servermeeting.domain.user.application.request.UserUpdateRequestDto
import uoslife.servermeeting.domain.user.application.response.UserFindResponseDto
import uoslife.servermeeting.domain.user.application.response.toResponse
import uoslife.servermeeting.domain.user.domain.entity.User
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

        updateUserData(existingUser, requestDto)

        return ResponseEntity.ok(id);
    }

    fun findUserByNickname(nickname: String): ResponseEntity<String> {
        val user = userRepository.findUserByNickname(nickname)
        val message = if (user == null) {
            "존재하지 않는 닉네임입니다."
        } else {
            "존재하는 닉네임입니다."
        }

        return ResponseEntity.ok(message)
    }

    private fun updateUserData(
        existingUser: User?,
        requestDto: UserUpdateRequestDto
    ) {
        existingUser?.let {
            if (requestDto.hasChanges()) {
                requestDto.birthYear?.let { birthYear -> it.birthYear = birthYear }
                requestDto.gender?.let { gender -> it.gender = gender }
                requestDto.name?.let { name -> it.name = name }
                requestDto.department?.let { department -> it.department = department }
                requestDto.studentType?.let { studentType -> it.studentType = studentType }
                requestDto.smoking?.let { smoking -> it.smoking = smoking }
                requestDto.spiritAnimal?.let { spiritAnimal -> it.spiritAnimal = spiritAnimal }
                requestDto.mbti?.let { mbti -> it.mbti = mbti }
                requestDto.interest?.let { interest -> it.interest = interest }
                requestDto.height?.let { height -> it.height = height }
                requestDto.nickname?.let { nickname -> it.nickname = nickname }

                userRepository.save(it)
            }
        }
    }
}
