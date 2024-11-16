package uoslife.servermeeting.user.service

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uoslife.servermeeting.meetingteam.exception.PreconditionFailedException
import uoslife.servermeeting.meetingteam.repository.UserTeamRepository
import uoslife.servermeeting.meetingteam.service.PaymentService
import uoslife.servermeeting.meetingteam.util.Validator
import uoslife.servermeeting.user.dao.UserDao
import uoslife.servermeeting.user.dto.request.CreateProfileRequest
import uoslife.servermeeting.user.dto.request.UserUpdateRequest
import uoslife.servermeeting.user.dto.response.UserFindResponse
import uoslife.servermeeting.user.entity.User
import uoslife.servermeeting.user.entity.UserInformation
import uoslife.servermeeting.user.exception.UserNotFoundException
import uoslife.servermeeting.user.repository.UserInformationRepository
import uoslife.servermeeting.user.repository.UserRepository

@Service
@Transactional
class UserService(
    private val userRepository: UserRepository,
    @Qualifier("portOneService") private val paymentService: PaymentService,
    private val userTeamRepository: UserTeamRepository,
    private val userInformationRepository: UserInformationRepository,
    private val userDao: UserDao,
    private val validator: Validator
) {
    companion object {
        private val logger = LoggerFactory.getLogger(UserService::class.java)
    }
    @Transactional
    fun createUserByEmail(email: String): Long {
        // 해당 유저가 처음 이용하는 유저면 유저 생성
        val user = getOrCreateUserByEmail(email)
        if (user.userInformation == null) {
            val newUserInformation = UserInformation(user = user)
            userInformationRepository.save(newUserInformation)
            user.userInformation = newUserInformation
            logger.info("[유저 생성] UOS EMAIL : ${user.email}")
        }
        return user.id!!
    }

    fun findUser(id: Long): UserFindResponse {
        val user = userDao.findUserAllInfo(id) ?: throw UserNotFoundException()

        return UserFindResponse.valueOf(user)
    }

    @Transactional
    fun updateUser(requestDto: UserUpdateRequest, id: Long) {
        val existingUser = userDao.findUserAllInfo(id) ?: throw UserNotFoundException()
        if (existingUser.userInformation == null) throw PreconditionFailedException()

        existingUser.update(requestDto)
        val validMBTI = validator.setValidMBTI(requestDto.mbti)
        requestDto.updateUserInformation(existingUser, validMBTI)
    }

    /**
     * id로 유저를 삭제합니다. 유저를 삭제하기 전, 외부키로 연결되어 있는 Payment와 UserTeam을 삭제합니다. UserTeam을 삭제하면 MeetingTeam
     * 1:1, 3:3 에서 모두 나오며 미팅팀은 고아 객체로 유지됨 (확정 X) Payment는 User를 NULL로 설정하고 STATUS를 변경하여 SOFT_DELETE
     * 진행합니다.
     */
    @Transactional
    fun deleteUserById(id: Long) {
        // 유저가 존재하는지 확인
        val user: User = userRepository.findByIdOrNull(id) ?: throw UserNotFoundException()
        if (user.userTeams.isNotEmpty()) {
            user.userTeams.forEach { userTeamRepository.delete(it) }
        }
        // 결제 소프트 삭제 우선 진행
        paymentService.deleteUserPayment(user)
        // 유저 삭제

        val deletedId = user.id
        userRepository.delete(user)
        logger.info("[유저 삭제] UserId : $deletedId")
    }

    fun isDuplicatedKakaoTalkId(kakaoTalkId: String): Boolean {
        if (userRepository.existsByKakaoTalkId(kakaoTalkId)) return true
        return false
    }

    fun getOrCreateUserByEmail(email: String): User {
        val user = userRepository.findByEmail(email)
        if (user != null) {
            return user
        }
        return userRepository.save(User.create(email = email))
    }

    @Transactional
    fun createProfile(requestDto: CreateProfileRequest, id: Long) {
        // 사용자 조회
        val user = userRepository.findById(id).orElseThrow { throw UserNotFoundException() }

        // 프로필 정보 업데이트
        //        user.createProfile(requestDto)

        // 변경된 사용자 저장
        userRepository.save(user)
    }
}
