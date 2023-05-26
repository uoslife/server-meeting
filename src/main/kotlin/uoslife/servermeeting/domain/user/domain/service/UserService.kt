package uoslife.servermeeting.domain.user.domain.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import uoslife.servermeeting.domain.user.application.request.UserUpdateRequest
import uoslife.servermeeting.domain.user.application.response.NicknameCheckResponse
import uoslife.servermeeting.domain.user.application.response.UserFindResponseDto
import uoslife.servermeeting.domain.user.application.response.UserUpdateResponse
import uoslife.servermeeting.domain.user.application.response.toResponse
import uoslife.servermeeting.domain.user.domain.entity.User
import uoslife.servermeeting.domain.user.domain.exception.ExistingUserNotFoundException
import uoslife.servermeeting.domain.user.domain.exception.UserNotFoundException
import uoslife.servermeeting.domain.user.domain.repository.UserRepository
import java.util.UUID

@Service
class UserService(
    private val userRepository: UserRepository) {

    fun findUser(id: UUID): ResponseEntity<UserFindResponseDto> {
        val user = userRepository.findByIdOrNull(id) ?: throw UserNotFoundException()
        return ResponseEntity.ok(user.toResponse())
    }

    fun updateUser(requestDto: UserUpdateRequest, id: UUID): ResponseEntity<UserUpdateResponse> {
        val existingUser = userRepository.findByIdOrNull(id) ?: throw ExistingUserNotFoundException()
        updateUserData(existingUser, requestDto)
        return ResponseEntity.ok(UserUpdateResponse("성공적으로 변경됐습니다"))
    }

    fun findUserByNickname(nickname: String): ResponseEntity<NicknameCheckResponse> {
        val user = userRepository.findUserByNickname(nickname)
        return ResponseEntity.ok(checkNicknameDuplication(user))
    }

    private fun checkNicknameDuplication(user: User?): NicknameCheckResponse {
        return if (user?.nickname == null) NicknameCheckResponse("존재하지 않는 닉네임입니다." ,false)
        else NicknameCheckResponse("존재하는 닉네임입니다.", true)
    }

    private fun updateUserData(
        existingUser: User?,
        requestDto: UserUpdateRequest
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
