package uoslife.servermeeting.user.service

import jakarta.servlet.http.HttpServletRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uoslife.servermeeting.global.auth.exception.UnauthorizedException
import uoslife.servermeeting.global.auth.jwt.TokenProvider
import uoslife.servermeeting.global.auth.service.AccountService
import uoslife.servermeeting.meetingteam.entity.MeetingTeam
import uoslife.servermeeting.meetingteam.repository.MeetingTeamRepository
import uoslife.servermeeting.meetingteam.repository.PaymentRepository
import uoslife.servermeeting.meetingteam.util.Validator
import uoslife.servermeeting.user.dao.UserDao
import uoslife.servermeeting.user.dto.request.UserUpdateRequest
import uoslife.servermeeting.user.dto.response.UserFindResponse
import uoslife.servermeeting.user.entity.User
import uoslife.servermeeting.user.entity.UserPersonalInformation
import uoslife.servermeeting.user.exception.UserNotFoundException
import uoslife.servermeeting.user.repository.UserRepository
import uoslife.servermeeting.verification.dto.University

@Service
@Transactional
class UserService(
    private val userRepository: UserRepository,
    private val paymentRepository: PaymentRepository,
    private val meetingTeamRepository: MeetingTeamRepository,
    private val userDao: UserDao,
    private val tokenProvider: TokenProvider,
    private val accountService: AccountService,
    private val validator: Validator
) {
    @Transactional
    fun createUser(requset: HttpServletRequest): Unit {
        val resolvedToken = tokenProvider.resolveToken(requset) ?: throw UnauthorizedException()

        // 계정 서비스에서 유저 정보 받아오기
        val accountUser = accountService.getAuthenticatedUserProfile(resolvedToken)
        if (accountUser.email.isNullOrBlank() || accountUser.realm == null)
            throw UserNotFoundException()

        // 해당 유저가 처음 이용하는 유저면 유저 생성
        // 그렇지 않으면 유저 조회
        val savedUser = getOrCreateUser(accountUser.id.toLong(), accountUser.realm.code)
    }

    fun findUser(id: Long): UserFindResponse {
        val user = userRepository.findByIdOrNull(id) ?: throw UserNotFoundException()

        return User.toResponse(user)
    }

    @Transactional
    fun updateUser(requestDto: UserUpdateRequest, id: Long): Unit {
        val existingUser = userRepository.findByIdOrNull(id) ?: throw UserNotFoundException()

        val userPersonalInformation: UserPersonalInformation =
            updateUserPersonalInformationWithDto(existingUser, requestDto)

        existingUser.update(requestDto, userPersonalInformation)
    }

    private fun updateUserPersonalInformationWithDto(
        existingUser: User,
        requestDto: UserUpdateRequest
    ): UserPersonalInformation {
        val validMBTI = validator.setValidMBTI(requestDto.mbti)
        return requestDto.toUserPersonalInformation(existingUser, validMBTI)
    }

    @Transactional
    fun resetUser(id: Long): ResponseEntity<Unit> {
        val user: User = userRepository.findByIdOrNull(id) ?: throw UserNotFoundException()

        val updatingUser: User =
            User(
                id = user.id,
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
    fun deleteUserById(id: Long): Unit {
        // 유저가 존재하는지 확인
        val user: User =
            userDao.findUserWithMeetingTeam(userId = id) ?: throw UserNotFoundException()

        // 유저 삭제
        userRepository.delete(user)

        // Payment 삭제
        paymentRepository.deleteByUser(user)

        val meetingTeam: MeetingTeam = user.team ?: return

        // 미팅팀 삭제(미팅팀에 유저가 혼자일 경우)
        if (meetingTeam.users.size == 1) {
            meetingTeamRepository.delete(meetingTeam)
        }
    }

    private fun getOrCreateUser(userId: Long, university: University): User {
        return userRepository.findByIdOrNull(userId)
            ?: userRepository.save(User.create(userId = userId, university = university))
    }
}
