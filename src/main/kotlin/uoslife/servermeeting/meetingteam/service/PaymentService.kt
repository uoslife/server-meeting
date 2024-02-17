package uoslife.servermeeting.meetingteam.service

import java.util.UUID
import uoslife.servermeeting.meetingteam.dto.request.PayappRequestDto
import uoslife.servermeeting.meetingteam.dto.response.PayappResponseDto

interface PaymentService {
    fun requestPayment(userUUID: UUID): PayappResponseDto.PayappRequestStatusResponse
    fun checkPayment(payappCheckStatusRequest: PayappRequestDto.PayappCheckStatusRequest)
    fun refundPaymentById(): Unit
    fun refundPayment(): Unit
}
