package uoslife.servermeeting.meetingteam.service.impl

import jakarta.transaction.Transactional
import java.util.*
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import uoslife.servermeeting.meetingteam.dto.request.PortOneRequestDto
import uoslife.servermeeting.meetingteam.dto.response.PortOneResponseDto
import uoslife.servermeeting.meetingteam.entity.Payment
import uoslife.servermeeting.meetingteam.entity.enums.PaymentStatus
import uoslife.servermeeting.meetingteam.entity.enums.TeamType
import uoslife.servermeeting.meetingteam.exception.MeetingTeamNotFoundException
import uoslife.servermeeting.meetingteam.exception.PhoneNumberNotFoundException
import uoslife.servermeeting.meetingteam.repository.PaymentRepository
import uoslife.servermeeting.meetingteam.service.PaymentService
import uoslife.servermeeting.user.exception.UserNotFoundException
import uoslife.servermeeting.user.repository.UserRepository

@Service
@Qualifier("PortOneService")
class PortOneService(
    private val userRepository: UserRepository,
    private val paymentRepository: PaymentRepository,
    @Value("\${portone.api.price.single}") private val singlePrice: Int,
    @Value("\${portone.api.price.triple}") private val triplePrice: Int,
) : PaymentService {

    @Transactional
    override fun requestPayment(
        userUUID: UUID,
        request: PortOneRequestDto.PortOneRequestPaymentRequest
    ): PortOneResponseDto.PortOneRequestPaymentResponse {
        val user = userRepository.findByIdOrNull(userUUID) ?: throw UserNotFoundException()
        val team = user.team ?: throw MeetingTeamNotFoundException()
        val phoneNumber = user.phoneNumber ?: throw PhoneNumberNotFoundException()

        val payment =
            Payment.createPayment(
                user = user,
                pg = request.pg,
                payMethod = request.payMethod,
                marchantUid = UUID.randomUUID().toString(),
                price =
                    when (team.type) {
                        TeamType.SINGLE -> singlePrice
                        TeamType.TRIPLE -> triplePrice
                    },
                status = PaymentStatus.REQUEST,
            )

        paymentRepository.save(payment)

        return PortOneResponseDto.PortOneRequestPaymentResponse(
            payment.marchantUid!!,
            payment.price!!,
            phoneNumber
        )
    }

    override fun checkPayment() {
        TODO("결제 검증")
    }

    override fun refundPaymentById() {
        TODO("특정 유저 결제 취소")
    }

    override fun refundPayment() {
        TODO("비매칭인원 전체 결제 취소")
    }
}
