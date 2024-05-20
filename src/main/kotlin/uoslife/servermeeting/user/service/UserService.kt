package uoslife.servermeeting.user.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uoslife.servermeeting.global.auth.service.UOSLIFEAccountService
import uoslife.servermeeting.meetingteam.service.PaymentService
import uoslife.servermeeting.meetingteam.service.impl.MeetingTeamService
import uoslife.servermeeting.meetingteam.util.Validator
import uoslife.servermeeting.user.dto.request.UserUpdateRequest
import uoslife.servermeeting.user.dto.response.UserFindResponse
import uoslife.servermeeting.user.entity.University
import uoslife.servermeeting.user.entity.User
import uoslife.servermeeting.user.entity.UserPersonalInformation
import uoslife.servermeeting.user.exception.EmailUnauthorizedException
import uoslife.servermeeting.user.exception.UserNotFoundException
import uoslife.servermeeting.user.repository.UserRepository

@Service
@Transactional
class UserService(
    private val userRepository: UserRepository,
    private val paymentService: PaymentService,
    private val meetingTeamService: MeetingTeamService,
    private val uoslifeAccountService: UOSLIFEAccountService,
    private val validator: Validator
) {
    @Transactional
    fun createUser(id: Long) {
        // 계정 서비스에서 유저 정보 받아오기
        val userProfile = uoslifeAccountService.getUserProfile(id)
        if (userProfile.email.isNullOrBlank() || userProfile.realm == null)
            throw EmailUnauthorizedException()

        // 해당 유저가 처음 이용하는 유저면 유저 생성
        // 그렇지 않으면 유저 조회
        getOrCreateUser(id, University.valueOf(userProfile.realm.code))
    }

    fun findUser(id: Long): UserFindResponse {
        val user = userRepository.findByIdOrNull(id) ?: throw UserNotFoundException()

        return User.toResponse(user)
    }

    @Transactional
    fun updateUser(requestDto: UserUpdateRequest, id: Long) {
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

    /**
     * id로 유저를 삭제합니다. 유저를 삭제하기 전, 외부키로 연결되어 있는 Payment와 MeetingTeam을 삭제합니다. MeetingTeam은
     * @ManyToOne이기 때문에 MeetingTeam에 있는 유저를 삭제합니다. 만약 삭제 시 MeetingTeam에 유저가 빈다면, MeetingTeam 또한
     * 삭제합니다.
     */
    @Transactional
    fun deleteUserById(id: Long): Unit {
        // 유저가 존재하는지 확인
        val user: User = userRepository.findByIdOrNull(id) ?: throw UserNotFoundException()

        // 유저 삭제
        userRepository.delete(user)

        // Payment 삭제
        paymentService.deleteUserPayment(user)

        meetingTeamService.deleteEmptyMeetingTeam(user.team ?: return)
    }

    fun isDuplicatedKakaoTalkId(kakaoTalkId: String): Boolean {
        if (userRepository.existsByKakaoTalkId(kakaoTalkId)) return true
        return false
    }

    private fun getOrCreateUser(userId: Long, university: University): User {
        return userRepository.findByIdOrNull(userId)
            ?: userRepository.save(User.create(userId = userId, university = university))
    }
}
