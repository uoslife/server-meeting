package uoslife.servermeeting.meetingteam.service.impl

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uoslife.servermeeting.meetingteam.dto.request.MeetingTeamInformationUpdateRequest
import uoslife.servermeeting.meetingteam.dto.request.MeetingTeamMessageUpdateRequest
import uoslife.servermeeting.meetingteam.dto.request.MeetingTeamPreferenceUpdateRequest
import uoslife.servermeeting.meetingteam.dto.response.MeetingTeamCodeResponse
import uoslife.servermeeting.meetingteam.dto.response.MeetingTeamInformationGetResponse
import uoslife.servermeeting.meetingteam.dto.response.MeetingTeamUserListGetResponse
import uoslife.servermeeting.meetingteam.entity.SingleMeetingTeam
import uoslife.servermeeting.meetingteam.entity.enums.TeamType
import uoslife.servermeeting.meetingteam.exception.*
import uoslife.servermeeting.meetingteam.repository.SingleMeetingTeamRepository
import uoslife.servermeeting.meetingteam.service.BaseMeetingService
import uoslife.servermeeting.meetingteam.service.PaymentService
import uoslife.servermeeting.meetingteam.service.util.MeetingServiceUtils
import uoslife.servermeeting.meetingteam.util.Validator
import uoslife.servermeeting.user.dao.UserDao
import uoslife.servermeeting.user.entity.User
import uoslife.servermeeting.user.exception.UserNotFoundException
import uoslife.servermeeting.user.repository.UserRepository

@Service
@Transactional(readOnly = true)
@Qualifier("singleMeetingService")
class SingleMeetingService(
    private val userRepository: UserRepository,
    private val singleMeetingTeamRepository: SingleMeetingTeamRepository,
    private val userDao: UserDao,
    private val meetingServiceUtils: MeetingServiceUtils,
    private val validator: Validator,
    @Qualifier("PortOneService") private val portOneService: PaymentService,
    @Value("\${app.season}") private val season: Int,
) : BaseMeetingService {

    @Transactional
    override fun createMeetingTeam(
        userId: Long,
        name: String?,
    ): MeetingTeamCodeResponse {
        val user = userRepository.findByIdOrNull(userId) ?: throw UserNotFoundException()
        validator.isUserAlreadyHaveSingleTeam(user)

        val meetingTeam = createSingleMeetingTeam(leader = user)
        user.singleTeam = meetingTeam

        val payment = portOneService.createPayment(TeamType.SINGLE)
        meetingTeam.payment = payment

        return MeetingTeamCodeResponse(code = null)
    }

    override fun joinMeetingTeam(
        userId: Long,
        code: String,
        isJoin: Boolean
    ): MeetingTeamUserListGetResponse? {
        throw InSingleMeetingTeamNoJoinTeamException()
    }

    override fun getMeetingTeamUserList(
        userId: Long,
        code: String
    ): MeetingTeamUserListGetResponse {
        throw InSingleMeetingTeamOnlyOneUserException()
    }

    @Transactional
    override fun updateMeetingTeamInformation(
        userId: Long,
        meetingTeamInformationUpdateRequest: MeetingTeamInformationUpdateRequest
    ) {
        val user = userDao.findUserWithMeetingTeam(userId = userId) ?: throw UserNotFoundException()
        val meetingTeam = user.singleTeam ?: throw MeetingTeamNotFoundException()

        val information = meetingTeamInformationUpdateRequest.toInformation(user.gender)

        meetingTeam.information = information
    }

    @Transactional
    override fun updateMeetingTeamPreference(
        userId: Long,
        meetingTeamPreferenceUpdateRequest: MeetingTeamPreferenceUpdateRequest
    ) {
        val user = userDao.findUserWithMeetingTeam(userId) ?: throw UserNotFoundException()
        val meetingTeam = user.singleTeam ?: throw MeetingTeamNotFoundException()

        val validMBTI = validator.setValidMBTI(meetingTeamPreferenceUpdateRequest.mbti)
        val preference = meetingTeamPreferenceUpdateRequest.toSinglePreference(validMBTI)

        meetingTeam.preference = preference
    }

    @Transactional
    override fun updateMeetingTeamMessage(
        userId: Long,
        meetingTeamMessageUpdateRequest: MeetingTeamMessageUpdateRequest
    ) {
        validator.isMessageLengthIsValid(meetingTeamMessageUpdateRequest.message)
        val user = userDao.findUserWithMeetingTeam(userId) ?: throw UserNotFoundException()
        val meetingTeam = user.singleTeam ?: throw MeetingTeamNotFoundException()

        val message = meetingTeamMessageUpdateRequest.message

        //        meetingTeam.message = message
    }

    override fun getMeetingTeamInformation(userId: Long): MeetingTeamInformationGetResponse {
        val user = userDao.findUserWithMeetingTeam(userId) ?: throw UserNotFoundException()
        val meetingTeam = user.singleTeam ?: throw MeetingTeamNotFoundException()

        val information = meetingTeam.information ?: throw InformationNotFoundException()
        val preference = meetingTeam.preference ?: throw PreferenceNotFoundException()

        return meetingServiceUtils.toMeetingTeamInformationGetResponse(
            user.gender,
            TeamType.SINGLE,
            user,
            information,
            preference,
            null,
            null
        )
    }

    @Transactional
    override fun deleteMeetingTeam(userId: Long) {
        val user = userDao.findUserWithMeetingTeam(userId) ?: throw UserNotFoundException()
        val meetingTeam = user.singleTeam ?: throw MeetingTeamNotFoundException()
        user.singleTeam = null

        singleMeetingTeamRepository.delete(meetingTeam)
    }

    @Transactional
    fun createSingleMeetingTeam(leader: User): SingleMeetingTeam {
        return singleMeetingTeamRepository.save(
            SingleMeetingTeam(
                season = season,
                leader = leader,
            ),
        )
    }
}
