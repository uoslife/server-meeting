package uoslife.servermeeting.user.service

import java.util.*
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uoslife.servermeeting.meetingteam.entity.MeetingTeam
import uoslife.servermeeting.meetingteam.exception.MeetingTeamNotFoundException
import uoslife.servermeeting.meetingteam.repository.MeetingTeamRepository
import uoslife.servermeeting.meetingteam.repository.PaymentRepository
import uoslife.servermeeting.user.dao.UserDao
import uoslife.servermeeting.user.dto.request.UserUpdateRequest
import uoslife.servermeeting.user.dto.response.UserFindResponse
import uoslife.servermeeting.user.entity.User
import uoslife.servermeeting.user.entity.UserPersonalInformation
import uoslife.servermeeting.user.exception.UserNotFoundException
import uoslife.servermeeting.user.repository.UserRepository

@Service
@Transactional
class UserService(
    private val userRepository: UserRepository,
    private val paymentRepository: PaymentRepository,
    private val meetingTeamRepository: MeetingTeamRepository,
    private val userDao: UserDao,
) {

    fun findUser(id: UUID): UserFindResponse {
        val user = userRepository.findByIdOrNull(id) ?: throw UserNotFoundException()

        return User.toResponse(user)
    }

    @Transactional
    fun updateUser(requestDto: UserUpdateRequest, id: UUID): Unit {
        val existingUser = userRepository.findByIdOrNull(id) ?: throw UserNotFoundException()

        val userPersonalInformation: UserPersonalInformation =
            updateUserPersonalInformationWithDto(existingUser, requestDto)

        existingUser.name = requestDto.name ?: existingUser.name
        existingUser.phoneNumber = requestDto.phoneNumber ?: existingUser.phoneNumber
        existingUser.kakaoTalkId = requestDto.kakaoTalkId ?: existingUser.kakaoTalkId
        existingUser.userPersonalInformation = userPersonalInformation
    }

    private fun updateUserPersonalInformationWithDto(
        existingUser: User,
        requestDto: UserUpdateRequest
    ): UserPersonalInformation {
        val userPersonalInformation =
            UserPersonalInformation(
                age = requestDto.age,
                gender = requestDto.gender,
                height = requestDto.height ?: existingUser.userPersonalInformation.height,
                studentType = requestDto.studentType,
                kakaoTalkId = requestDto.kakaoTalkId,
                university = existingUser.userPersonalInformation.university,
                department = requestDto.department,
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
            )
        updatingUser.userPersonalInformation.university = user.userPersonalInformation.university
        userRepository.save(updatingUser)

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }

    /**
     * id로 유저를 삭제합니다. 유저를 삭제하기 전, 외부키로 연결되어 있는 Payment와 MeetingTeam을 삭제합니다. MeetingTeam은
     * @ManyToOne이기 때문에 MeetingTeam에 있는 유저를 삭제합니다. 만약 삭제 시 MeetingTeam에 유저가 빈다면, MeetingTeam 또한
     * 삭제합니다.
     */
    @Transactional
    fun deleteUserById(id: UUID): Unit {
        // 유저가 존재하는지 확인
        val user: User =
            userDao.findUserWithMeetingTeam(userId = id) ?: throw UserNotFoundException()
        val meetingTeam: MeetingTeam = user.team ?: throw MeetingTeamNotFoundException()

        // 유저 삭제
        userRepository.delete(user)

        // Payment 삭제
        paymentRepository.deleteByUser(user)

        // 미팅팀 삭제(미팅팀에 유저가 혼자일 경우)
        if (meetingTeam.users.size == 1) {
            meetingTeamRepository.delete(meetingTeam)
        }
    }
}
