package uoslife.servermeeting.user.service

import java.util.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import uoslife.servermeeting.global.auth.jwt.TokenProvider
import uoslife.servermeeting.meetingteam.dto.response.MeetingTeamCodeResponse
import uoslife.servermeeting.meetingteam.entity.MeetingTeam
import uoslife.servermeeting.meetingteam.entity.Payment
import uoslife.servermeeting.meetingteam.entity.enums.PayMethod
import uoslife.servermeeting.meetingteam.entity.enums.PaymentGateway
import uoslife.servermeeting.meetingteam.entity.enums.PaymentStatus
import uoslife.servermeeting.meetingteam.entity.enums.TeamType
import uoslife.servermeeting.meetingteam.repository.PaymentRepository
import uoslife.servermeeting.meetingteam.service.impl.SingleMeetingService
import uoslife.servermeeting.meetingteam.service.impl.TripleMeetingService
import uoslife.servermeeting.user.entity.User
import uoslife.servermeeting.user.repository.UserRepository
import uoslife.servermeeting.verification.dto.University

@SpringBootTest
class UserServiceTest
@Autowired
constructor(
    private val userService: UserService,
    private val userRepository: UserRepository,
    private val paymentRepository: PaymentRepository,
    private val singleMeetingService: SingleMeetingService,
    private val tripleMeetingService: TripleMeetingService,
    private val tokenProvider: TokenProvider,
) {
    @Test
    @DisplayName("미팅팀(Single)에 유저 삭제 시, 미팅팀과 payment 또한 삭제되어야 한다.")
    @Transactional
    fun deleteUserByIdSingleMeetingTeam() {
        // given
        val email: String = "gustmd5715@uos.ac.kr"
        val user: User = User.create(email, University.UOS)
        val savedUser: User = userRepository.save(user)

        val payment: Payment =
            Payment.createPayment(
                user = savedUser,
                pg = PaymentGateway.WELCOME_PAYMENTS,
                payMethod = PayMethod.card,
                marchantUid = "TEST",
                price = 10000,
                status = PaymentStatus.SUCCESS
            )
        paymentRepository.save(payment)
        savedUser.payment = payment
        assertEquals(paymentRepository.findAll().size, 1)

        val savedMeetingTeam: MeetingTeam =
            singleMeetingService.createDefaultMeetingTeam(savedUser, TeamType.SINGLE)
        savedUser.team = savedMeetingTeam

        // when
        userService.deleteUserById(savedUser.id!!)

        // then -> 유저가 삭제되있어야 하고, Payment DB에는 아무 값도 없어야 하고, MeetingTeam DB에는 유저2만 있어야 한다.
        val payments: List<Payment> = paymentRepository.findAll()
        assertEquals(payments.size, 0)
        assertEquals(userRepository.findById(savedUser.id!!).isPresent, false)
        assertEquals(savedUser.team?.users?.size, 0)
    }

    @Test
    @DisplayName("미팅팀(Triple)에 유저 삭제 시, payment를 삭제하고 미팅팀에서 유저를 삭제한다.(만약 미팅팀에 아무도 없다면 미팅팀 또한 삭제한다)")
    @Transactional
    fun deleteUserByIdTripleMeetingTeam() {
        // given
        val email: String = "gustmd5715@uos.ac.kr"
        val user: User = User.create(email, University.UOS)
        val savedUser: User = userRepository.save(user)

        val email2: String = "otherOne@uos.ac.kr"
        val user2: User = User.create(email2, University.UOS)
        val savedUser2: User = userRepository.save(user2)

        val payment: Payment =
            Payment.createPayment(
                user = savedUser,
                pg = PaymentGateway.WELCOME_PAYMENTS,
                payMethod = PayMethod.card,
                marchantUid = "TEST",
                price = 10000,
                status = PaymentStatus.SUCCESS
            )
        paymentRepository.save(payment)

        val code: MeetingTeamCodeResponse =
            tripleMeetingService.createMeetingTeam(
                savedUser.id!!,
                teamType = TeamType.TRIPLE,
                name = "TEST"
            )!!
        tripleMeetingService.joinMeetingTeam(savedUser2.id!!, code.code!!, false)

        // when
        userService.deleteUserById(savedUser.id!!)

        // then -> 유저가 삭제되있어야 하고, Payment DB에는 아무 값도 없어야 하고, MeetingTeam DB에는 유저2만 있어야 한다.
        assertEquals(userRepository.findById(savedUser.id!!).isPresent, false)
    }

    //    @Test
    //    @DisplayName("유저 email unique(false)가 잘 작동하는지 확인한다.")
    //    fun userEmailUniqueTest(){
    //        // given
    //        val user1: User = User(
    //            id = UUID.randomUUID(),
    //            name = "현승",
    //            email = "gustmd5715@uos.ac.kr"
    //        )
    //        val user2: User = User(
    //            id = UUID.randomUUID(),
    //            name = "인규",
    //            email = "gustmd5715@uos.ac.kr"
    //        )
    //
    //        // when
    //        userRepository.save(user1)
    //        userRepository.save(user2)
    //
    //
    //        // then
    //
    //    }
}
