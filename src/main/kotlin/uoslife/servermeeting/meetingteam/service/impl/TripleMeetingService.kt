package uoslife.servermeeting.meetingteam.service.impl

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uoslife.servermeeting.meetingteam.dao.UserTeamDao
import uoslife.servermeeting.meetingteam.dto.request.MeetingTeamInfoUpdateRequest
import uoslife.servermeeting.meetingteam.dto.response.MeetingTeamCodeResponse
import uoslife.servermeeting.meetingteam.dto.response.MeetingTeamInformationGetResponse
import uoslife.servermeeting.meetingteam.dto.response.MeetingTeamLeaderNameResponse
import uoslife.servermeeting.meetingteam.dto.vo.MeetingTeamUsers
import uoslife.servermeeting.meetingteam.entity.MeetingTeam
import uoslife.servermeeting.meetingteam.entity.UserTeam
import uoslife.servermeeting.meetingteam.entity.enums.TeamType
import uoslife.servermeeting.meetingteam.exception.*
import uoslife.servermeeting.meetingteam.repository.MeetingTeamRepository
import uoslife.servermeeting.meetingteam.repository.PreferenceRepository
import uoslife.servermeeting.meetingteam.repository.UserTeamRepository
import uoslife.servermeeting.meetingteam.service.BaseMeetingService
import uoslife.servermeeting.meetingteam.service.util.MeetingDtoConverter
import uoslife.servermeeting.meetingteam.util.UniqueCodeGenerator
import uoslife.servermeeting.meetingteam.util.Validator
import uoslife.servermeeting.payment.service.PaymentService
import uoslife.servermeeting.user.entity.User
import uoslife.servermeeting.user.service.UserService

@Service
@Transactional(readOnly = true)
@Qualifier("tripleMeetingService")
class TripleMeetingService(
    private val userService: UserService,
    private val userTeamDao: UserTeamDao,
    private val preferenceRepository: PreferenceRepository,
    private val uniqueCodeGenerator: UniqueCodeGenerator,
    private val meetingTeamRepository: MeetingTeamRepository,
    private val validator: Validator,
    private val userTeamRepository: UserTeamRepository,
    @Qualifier("PortOneService") private val paymentService: PaymentService,
    @Value("\${app.season}") private val season: Int,
) : BaseMeetingService {

    companion object {
        private val logger = LoggerFactory.getLogger(TripleMeetingService::class.java)
    }

    @Transactional
    override fun createMeetingTeam(
        userId: Long,
    ): MeetingTeamCodeResponse {
        val user = userService.getUser(userId)

        validator.isUserAlreadyHaveTripleTeam(user)

        val code = uniqueCodeGenerator.getUniqueTeamCode()
        val meetingTeam = createDefaultMeetingTeam(user, code, TeamType.TRIPLE)

        userTeamDao.saveUserTeam(meetingTeam, user, true)
        logger.info("[유저 3:3 팀 생성] User ID : $userId Triple Team ID : ${meetingTeam.id}")
        return MeetingTeamCodeResponse(code)
    }

    @Transactional
    override fun joinMeetingTeam(
        userId: Long,
        code: String,
    ): MeetingTeamLeaderNameResponse {
        val user = userService.getUser(userId)

        validator.isTeamCodeValid(code)
        validator.isUserAlreadyHaveTripleTeam(user)

        val meetingTeam =
            meetingTeamRepository.findByCode(code) ?: throw MeetingTeamNotFoundException()

        validator.isTeamFull(meetingTeam)

        val newUserTeam = UserTeam.createUserTeam(meetingTeam, user, false)
        userTeamRepository.save(newUserTeam)

        logger.info("[3:3팀 입장] User ID : $userId Triple Team ID : ${meetingTeam.id}")
        return MeetingTeamLeaderNameResponse(meetingTeam.name)
    }

    override fun getMeetingTeamUserList(code: String): MeetingTeamLeaderNameResponse {
        validator.isTeamCodeValid(code)

        val meetingTeam =
            meetingTeamRepository.findByCode(code) ?: throw MeetingTeamNotFoundException()

        val meetingTeamUsers = MeetingTeamUsers(meetingTeam.userTeams)
        return MeetingTeamLeaderNameResponse(meetingTeamUsers.getLeaderName())
    }

    @Transactional
    override fun updateMeetingTeamInfo(
        userId: Long,
        meetingTeamInfoUpdateRequest: MeetingTeamInfoUpdateRequest
    ) {
        validator.isTeamNameInvalid(meetingTeamInfoUpdateRequest.name)

        val user = userService.getUser(userId)
        val meetingUserTeam = getUserTripleMeetingUserTeam(user)
        if (!meetingUserTeam.isLeader) throw OnlyTeamLeaderCanUpdateTeamInformationException()

        val meetingTeam = meetingUserTeam.team

        val newPreference = meetingTeamInfoUpdateRequest.toTriplePreference(meetingTeam)
        meetingTeam.preference?.let {
            meetingTeam.preference = null
            preferenceRepository.delete(it)
            preferenceRepository.flush()
        } // 분리된 트랜잭션 호출
        meetingTeam.preference = newPreference
        meetingTeam.name = meetingTeamInfoUpdateRequest.name
    }

    override fun getMeetingTeamInformation(userId: Long): MeetingTeamInformationGetResponse {
        val user = userService.getUser(userId)
        val meetingTeam = getUserTripleMeetingUserTeam(user).team

        val userTeamsWithInfo = userTeamDao.findAllUserTeamWithUserInfoFromMeetingTeam(meetingTeam)
        val preference = meetingTeam.preference ?: throw PreferenceNotFoundException()

        return MeetingDtoConverter.toMeetingTeamInformationGetResponse(
            meetingTeam.gender,
            TeamType.TRIPLE,
            userTeamsWithInfo,
            preference,
            meetingTeam.name,
            null
        )
    }

    @Transactional
    override fun deleteMeetingTeam(userId: Long) {
        val user = userService.getUser(userId)
        val meetingUserTeam = getUserTripleMeetingUserTeam(user)

        if (!meetingUserTeam.isLeader) throw OnlyTeamLeaderCanDeleteTeamException()

        val meetingTeam = meetingUserTeam.team
        checkTeamPayment(user)

        meetingTeam.userTeams.forEach { userTeamRepository.delete(it) }
        meetingTeamRepository.delete(meetingTeam)
        logger.info("[3:3 미팅 팀 삭제] User ID : $userId")
    }

    private fun checkTeamPayment(user: User) {
        val successPayment = paymentService.getSuccessPayment(user.id!!, teamType = TeamType.TRIPLE)

        if (successPayment != null) {
            paymentService.refundPaymentByToken(user.id!!, TeamType.SINGLE)
        }

        user.payments?.forEach { payment -> payment.removeMeetingTeam() }
    }

    @Transactional
    fun createDefaultMeetingTeam(leader: User, code: String, teamType: TeamType): MeetingTeam {
        return meetingTeamRepository.save(
            MeetingTeam(
                season = season,
                code = code,
                type = teamType,
                gender = leader.gender ?: throw GenderNotUpdatedException()
            ),
        )
    }

    private fun getUserTripleMeetingUserTeam(user: User): UserTeam {
        return userTeamDao.findByUserWithMeetingTeam(user, TeamType.TRIPLE)
            ?: throw MeetingTeamNotFoundException()
    }
}
