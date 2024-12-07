package uoslife.servermeeting.user.service

import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Lazy
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uoslife.servermeeting.global.auth.security.JwtTokenProvider
import uoslife.servermeeting.global.auth.util.CookieUtils
import uoslife.servermeeting.meetingteam.dao.UserTeamDao
import uoslife.servermeeting.meetingteam.entity.MeetingTeam
import uoslife.servermeeting.meetingteam.entity.UserTeam
import uoslife.servermeeting.meetingteam.entity.enums.TeamType
import uoslife.servermeeting.meetingteam.repository.UserTeamRepository
import uoslife.servermeeting.meetingteam.service.BaseMeetingService
import uoslife.servermeeting.meetingteam.util.Validator
import uoslife.servermeeting.payment.service.PaymentService
import uoslife.servermeeting.user.command.TeamBranch
import uoslife.servermeeting.user.command.UserCommand
import uoslife.servermeeting.user.dao.UserDao
import uoslife.servermeeting.user.dto.response.UserBranchResponse
import uoslife.servermeeting.user.entity.User
import uoslife.servermeeting.user.entity.UserInformation
import uoslife.servermeeting.user.entity.enums.StudentType
import uoslife.servermeeting.user.exception.*
import uoslife.servermeeting.user.repository.UserInformationRepository
import uoslife.servermeeting.user.repository.UserRepository

@Service
@Transactional(readOnly = true)
class UserService(
    private val userRepository: UserRepository,
    private val userTeamRepository: UserTeamRepository,
    private val userInformationRepository: UserInformationRepository,
    private val userDao: UserDao,
    private val userTeamDao: UserTeamDao,
    private val validator: Validator,
    private val cookieUtils: CookieUtils,
    private val jwtTokenProvider: JwtTokenProvider,
    @Qualifier("portOneService") private val paymentService: PaymentService,
    @Lazy @Qualifier("singleMeetingService") private val singleMeetingService: BaseMeetingService,
    @Lazy @Qualifier("tripleMeetingService") private val tripleMeetingService: BaseMeetingService
) {
    companion object {
        private val logger = LoggerFactory.getLogger(UserService::class.java)
    }

    fun getUserByEmail(email: String): User {
        return userRepository.findByEmail(email) ?: throw UserNotFoundException()
    }

    @Transactional
    fun createUserByEmail(email: String): User {
        val user = userRepository.save(User.create(email = email))
        val userInformation = userInformationRepository.save(UserInformation(user = user))
        user.userInformation = userInformation
        return user
    }

    fun getUser(userId: Long): User {
        val user = userRepository.findByIdOrNull(userId)
        if (user == null) {
            logger.info("[유저 없음]: UserID : $userId")
            throw UserNotFoundException()
        }
        return user
    }

    fun getUserDetailedInformation(userId: Long): User {
        return userDao.findUserProfile(userId) ?: throw UserNotFoundException()
    }

    @Transactional
    fun updateUserInformation(command: UserCommand.UpdateUserInformation): User {
        command.mbti = validator.setValidMBTI(command.mbti)
        val user = getUser(command.userId)
        return upsertUserInformation(user, command)
    }

    @Transactional
    fun updateUserPersonalInformation(command: UserCommand.UpdateUserPersonalInformation): User {
        if (command.kakaoTalkId != null) {
            isDuplicatedKakaoTalkId(command.userId, command.kakaoTalkId)
        }
        if (command.phoneNumber != null) {
            isDuplicatedPhoneNumber(command.userId, command.phoneNumber)
        }

        val user = getUser(command.userId)
        return updateUserProfile(user, command)
    }

    private fun isDuplicatedPhoneNumber(userId: Long, phoneNumber: String): Boolean {
        val user = getUser(userId)
        if (phoneNumber != user.phoneNumber) {
            if (userRepository.existsByPhoneNumber(phoneNumber))
                throw PhoneNumberDuplicationException()
        }
        return true
    }

    /**
     * id로 유저를 삭제합니다. 유저를 삭제하기 전 진행사항 유저의 1:1 미팅팀 삭제, 유저 3:3 미팅팀 삭제 (팅장일 경우), 유저 3:3 미팅팀 탈퇴 (팅원일 경우)
     * 외부키로 연결되어 있는 Payment를 삭제합니다. 결제가 이뤄진 경우 진행합니다.
     */
    @Transactional
    fun deleteUserById(userId: Long, response: HttpServletResponse) {
        // 유저가 존재하는지 확인
        val user: User = getUser(userId)
        // 유저 미팅팀 삭제
        if (user.userTeams.isNotEmpty()) {
            user.userTeams.forEach { deleteUserMeetingTeam(userId, it) }
        }
        // 결제 소프트 삭제 우선 진행
        paymentService.deleteUserPayment(user)
        // 유저 삭제 진행
        val deletedEmail = user.email

        deleteRefreshInfo(user, response)
        userRepository.delete(user)
        logger.info("[유저 삭제] email: $deletedEmail")
    }

    private fun deleteRefreshInfo(user: User, response: HttpServletResponse) {
        cookieUtils.deleteRefreshTokenCookie(response)
        jwtTokenProvider.deleteRefreshToken(user.id!!)
    }

    private fun deleteUserMeetingTeam(userId: Long, userTeam: UserTeam) {
        when (userTeam.team.type) {
            TeamType.SINGLE -> {
                singleMeetingService.deleteMeetingTeam(userId)
                userTeamRepository.delete(userTeam)
            }
            TeamType.TRIPLE -> {
                if (userTeam.isLeader) {
                    tripleMeetingService.deleteMeetingTeam(userId)
                }
                userTeamRepository.delete(userTeam)
            }
        }
    }

    fun isDuplicatedKakaoTalkId(userId: Long, kakaoTalkId: String): Boolean {
        val user = getUser(userId)
        if (kakaoTalkId != user.kakaoTalkId) {
            if (userRepository.existsByKakaoTalkId(kakaoTalkId))
                throw KakaoTalkIdDuplicationException()
        }
        return true
    }

    private fun upsertUserInformation(
        user: User,
        command: UserCommand.UpdateUserInformation
    ): User {
        val information: UserInformation =
            user.userInformation ?: throw UserInformationNotFoundException()

        information.smoking = command.smoking ?: information.smoking
        information.mbti = command.mbti ?: information.mbti
        information.interest = command.interest ?: information.interest
        information.height = command.height ?: information.height
        information.age = command.age ?: information.age
        information.studentNumber = command.studentNumber ?: information.studentNumber
        information.department = command.department ?: information.department
        information.eyelidType = command.eyelidType ?: information.eyelidType
        information.appearanceType = command.appearanceType ?: information.appearanceType
        information.studentType = command.studentType ?: information.studentType

        if (
            information.studentType == StudentType.GRADUATE ||
                information.studentType == StudentType.POSTGRADUATE
        ) {
            information.department = null
            information.studentNumber = null
        }
        return user
    }

    private fun updateUserProfile(
        user: User,
        command: UserCommand.UpdateUserPersonalInformation
    ): User {
        user.name = command.name ?: user.name
        user.phoneNumber = command.phoneNumber ?: user.phoneNumber
        user.kakaoTalkId = command.kakaoTalkId ?: user.kakaoTalkId
        command.gender?.let {
            if (user.gender == null) {
                user.gender = command.gender
            } else if (command.gender != user.gender) {
                throw GenderNotUpdatableException()
            }
        }
        return user
    }

    fun getUserMeetingTeamBranch(userId: Long): UserBranchResponse {
        val user = getUser(userId)

        val userTeams =
            userTeamDao.findAllUserTeamWithMeetingTeam(user)
                ?: return UserBranchResponse(
                    singleTeamBranch = TeamBranch.NOT_CREATED,
                    tripleTeamBranch = TeamBranch.NOT_CREATED
                )

        return determineMeetingTeamStatus(userId, userTeams)
    }

    private fun determineMeetingTeamStatus(
        userId: Long,
        userTeams: List<UserTeam>
    ): UserBranchResponse {
        var singleTeamBranch = TeamBranch.NOT_CREATED
        var tripleTeamBranch = TeamBranch.NOT_CREATED

        userTeams.forEach { userTeam ->
            when (userTeam.team.type) {
                TeamType.SINGLE -> singleTeamBranch = determineTeamBranch(userId, TeamType.SINGLE)
                TeamType.TRIPLE ->
                    tripleTeamBranch =
                        if (!userTeam.isLeader) {
                            determineTeamBranch(userTeam.team)
                        } else {
                            determineTeamBranch(userId, TeamType.TRIPLE)
                        }
            }
        }
        return UserBranchResponse(singleTeamBranch, tripleTeamBranch)
    }

    private fun determineTeamBranch(userId: Long, teamType: TeamType): TeamBranch {
        paymentService.getSuccessPayment(userId, teamType)?.let {
            return TeamBranch.COMPLETED
        }
        return TeamBranch.JUST_CREATED
    }

    private fun determineTeamBranch(meetingTeam: MeetingTeam): TeamBranch {
        paymentService.getSuccessPaymentFromMeetingTeam(meetingTeam)?.let {
            return TeamBranch.COMPLETED
        }
        return TeamBranch.JOINED
    }

    @Transactional
    fun getOrCreateUser(email: String): User {
        return try {
            getUserByEmail(email)
        } catch (e: UserNotFoundException) {
            createUserByEmail(email)
        }
    }
}
