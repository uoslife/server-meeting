package uoslife.servermeeting.user.service

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uoslife.servermeeting.meetingteam.dao.UserTeamDao
import uoslife.servermeeting.meetingteam.entity.UserTeam
import uoslife.servermeeting.meetingteam.entity.enums.TeamType
import uoslife.servermeeting.meetingteam.repository.UserTeamRepository
import uoslife.servermeeting.meetingteam.service.BaseMeetingService
import uoslife.servermeeting.meetingteam.util.Validator
import uoslife.servermeeting.payment.dao.PaymentDao
import uoslife.servermeeting.payment.service.PaymentService
import uoslife.servermeeting.user.command.TeamBranch
import uoslife.servermeeting.user.command.UserCommand
import uoslife.servermeeting.user.dao.UserDao
import uoslife.servermeeting.user.dto.response.UserBranchResponse
import uoslife.servermeeting.user.entity.User
import uoslife.servermeeting.user.exception.KakaoTalkIdDuplicationException
import uoslife.servermeeting.user.exception.UserNotFoundException
import uoslife.servermeeting.user.repository.UserRepository

@Service
@Transactional(readOnly = true)
class UserService(
    private val userRepository: UserRepository,
    @Qualifier("portOneService") private val paymentService: PaymentService,
    private val userTeamRepository: UserTeamRepository,
    private val userDao: UserDao,
    private val userTeamDao: UserTeamDao,
    private val paymentDao: PaymentDao,
    private val validator: Validator,
    @Qualifier("singleMeetingService") private val singleMeetingService: BaseMeetingService,
    @Qualifier("tripleMeetingService") private val tripleMeetingService: BaseMeetingService
) {
    companion object {
        private val logger = LoggerFactory.getLogger(UserService::class.java)
    }

    fun getUserByEmail(email: String): User {
        return userRepository.findByEmail(email) ?: throw UserNotFoundException()
    }

    @Transactional
    fun createUserByEmail(email: String): User {
        return userRepository.save(User.create(email = email))
    }

    fun getUser(userId: Long): User {
        return userRepository.findByIdOrNull(userId) ?: throw UserNotFoundException()
    }

    fun getUserProfile(userId: Long): User {
        return userDao.findUserProfile(userId) ?: throw UserNotFoundException()
    }

    @Transactional
    fun updateUserInformation(command: UserCommand.UpdateUserInformation): User {
        command.mbti = validator.setValidMBTI(command.mbti)
        val updated: Long = userDao.updateUserInformation(command)
        return userDao.findUserProfile(command.userId) ?: throw UserNotFoundException()
    }

    @Transactional
    fun updateUserPersonalInformation(command: UserCommand.UpdateUserPersonalInformation): User {
        if (command.kakaoTalkId != null) {
            isDuplicatedKakaoTalkId(command.kakaoTalkId)
        }
        val updateUserPersonalInformation = userDao.updateUserPersonalInformation(command)
        return userRepository.findByIdOrNull(command.userId) ?: throw UserNotFoundException()
    }

    /**
     * id로 유저를 삭제합니다. 유저를 삭제하기 전, 외부키로 연결되어 있는 Payment와 UserTeam을 삭제합니다. UserTeam을 삭제하면 MeetingTeam
     * 1:1, 3:3 에서 모두 나오며 미팅팀은 고아 객체로 유지됨 (확정 X) Payment는 User를 NULL로 설정하고 STATUS를 변경하여 SOFT_DELETE
     * 진행합니다.
     */
    // TODO 팀이 있는 상태로 한명이 나가면 팀 전체가 삭제 되기 때문에 팀 삭제 로직을 먼저 실행햐여야 함
    @Transactional
    fun deleteUserById(userId: Long) {
        // 유저가 존재하는지 확인
        val user: User = userRepository.findByIdOrNull(userId) ?: throw UserNotFoundException()
        // 유저 미팅팀 삭제
        if (user.userTeams.isNotEmpty()) {
            user.userTeams.forEach { deleteUserMeetingTeam(userId, it) }
        }
        // 결제 소프트 삭제 우선 진행
        paymentService.deleteUserPayment(user)
        // 유저 삭제 진행
        val deletedId = user.id
        userRepository.delete(user)
        logger.info("[유저 삭제 완료] UserId : $deletedId")
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

    fun isDuplicatedKakaoTalkId(kakaoTalkId: String): Boolean {
        if (userRepository.existsByKakaoTalkId(kakaoTalkId)) throw KakaoTalkIdDuplicationException()
        return true
    }

    fun getUserMeetingTeamBranch(userId: Long): UserBranchResponse {
        val user: User = userRepository.findByIdOrNull(userId) ?: throw UserNotFoundException()

        val userTeams =
            userTeamDao.findUserTeamWithMeetingTeam(user)
                ?: return UserBranchResponse(
                    singleTeamBranch = TeamBranch.NOT_CREATED,
                    tripleTeamBranch = TeamBranch.NOT_CREATED
                )

        return getMeetingTeamStatus(userId, userTeams)
    }

    private fun getMeetingTeamStatus(userId: Long, userTeams: List<UserTeam>): UserBranchResponse {
        var singleTeamBranch = TeamBranch.NOT_CREATED
        var tripleTeamBranch = TeamBranch.NOT_CREATED

        userTeams.forEach { userTeam ->
            when (userTeam.team.type) {
                TeamType.SINGLE -> {
                    singleTeamBranch = TeamBranch.JUST_CREATED
                    paymentDao
                        .getSuccessPaymentFromUserIdAndTeamType(userId, TeamType.SINGLE)
                        ?.let { singleTeamBranch = TeamBranch.COMPLETED }
                }
                TeamType.TRIPLE -> {
                    tripleTeamBranch = TeamBranch.JUST_CREATED
                    paymentDao
                        .getSuccessPaymentFromUserIdAndTeamType(userId, TeamType.TRIPLE)
                        ?.let { tripleTeamBranch = TeamBranch.COMPLETED }
                }
            }
        }

        return UserBranchResponse(singleTeamBranch, tripleTeamBranch)
    }
}
