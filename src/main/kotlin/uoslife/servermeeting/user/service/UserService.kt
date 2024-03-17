package uoslife.servermeeting.user.service

import java.util.*
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uoslife.servermeeting.user.dto.request.TosDto
import uoslife.servermeeting.user.dto.request.UserUpdateRequest
import uoslife.servermeeting.user.dto.response.UserFindResponseDto
import uoslife.servermeeting.user.dto.response.toResponse
import uoslife.servermeeting.user.entity.Tos
import uoslife.servermeeting.user.entity.User
import uoslife.servermeeting.user.entity.UserPersonalInformation
import uoslife.servermeeting.user.exception.ExistingUserNotFoundException
import uoslife.servermeeting.user.exception.UserNotFoundException
import uoslife.servermeeting.user.repository.TosRepository
import uoslife.servermeeting.user.repository.UserRepository

@Service
@Transactional
class UserService(
    private val userRepository: UserRepository,
    private val tosRepository: TosRepository,
) {

    fun findUser(id: UUID): ResponseEntity<UserFindResponseDto> {
        val user = userRepository.findByIdOrNull(id) ?: throw UserNotFoundException()
        return ResponseEntity.ok(user.toResponse())
    }

    @Transactional
    fun updateUser(requestDto: UserUpdateRequest, id: UUID): ResponseEntity<Unit> {
        val existingUser =
            userRepository.findByIdOrNull(id) ?: throw ExistingUserNotFoundException()

        val userPersonalInformation: UserPersonalInformation = updateUserPersonalInformationWithDto(existingUser, requestDto)

        existingUser.name = requestDto.name ?: existingUser.name
        existingUser.phoneNumber = requestDto.phoneNumber ?: existingUser.phoneNumber
        existingUser.kakaoTalkId = requestDto.kakaoTalkId ?: existingUser.kakaoTalkId
        existingUser.userPersonalInformation = userPersonalInformation
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }

    private fun updateUserPersonalInformationWithDto(existingUser: User, requestDto: UserUpdateRequest): UserPersonalInformation{
        val userPersonalInformation =
            UserPersonalInformation(
                age = requestDto.age ?: existingUser.userPersonalInformation.age,
                gender = requestDto.gender ?: existingUser.userPersonalInformation.gender,
                height = requestDto.height ?: existingUser.userPersonalInformation.height,
                studentType = requestDto.studentType
                    ?: existingUser.userPersonalInformation.studentType,
                kakaoTalkId = requestDto.kakaoTalkId
                    ?: existingUser.userPersonalInformation.kakaoTalkId,
                university = existingUser.userPersonalInformation.university,
                department = requestDto.department
                    ?: existingUser.userPersonalInformation.department,
                religion = requestDto.religion ?: existingUser.userPersonalInformation.religion,
                drinkingMin = requestDto.drinkingMin
                    ?: existingUser.userPersonalInformation.drinkingMin,
                drinkingMax = requestDto.drinkingMax
                    ?: existingUser.userPersonalInformation.drinkingMax,
                smoking = requestDto.smoking ?: existingUser.userPersonalInformation.smoking,
                spiritAnimal = requestDto.spiritAnimal
                    ?: existingUser.userPersonalInformation.spiritAnimal,
                mbti = requestDto.mbti ?: existingUser.userPersonalInformation.mbti,
                interest = requestDto.interest ?: existingUser.userPersonalInformation.interest,
                message = requestDto.message ?: existingUser.userPersonalInformation.message,
            )

        return userPersonalInformation
    }

    @Transactional
    fun resetUser(id: UUID): ResponseEntity<Unit> {
        val user: User = userRepository.findByIdOrNull(id) ?: throw UserNotFoundException()

        val updatingUser: User =
            User(
                id = user.id,
                email = user.email,
                payment = user.payment,
                tos = user.tos,
            )
        updatingUser.userPersonalInformation.university = user.userPersonalInformation.university
        userRepository.save(updatingUser)

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }

    @Transactional
    fun setTos(uuid: UUID, tosDto: TosDto) {
        val user: User = userRepository.findByIdOrNull(uuid) ?: throw UserNotFoundException()

        // dto에서 entity로 변경
        val tos: Tos = TosDto.toEntity(tosDto)
        val savedTos: Tos = tosRepository.save(tos)

        user.tos = savedTos
    }
}
