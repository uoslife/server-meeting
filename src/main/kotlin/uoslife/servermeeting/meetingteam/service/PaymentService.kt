package uoslife.servermeeting.meetingteam.service

import java.util.*
import uoslife.servermeeting.meetingteam.dto.request.PaymentRequestDto
import uoslife.servermeeting.meetingteam.dto.response.PaymentResponseDto

interface PaymentService {
    fun requestPayment(
        userId: Long,
        paymentRequestPaymentRequest: PaymentRequestDto.PaymentRequestRequest
    ): PaymentResponseDto.PaymentRequestResponse
    fun checkPayment(
        userId: Long,
        paymentCheckRequest: PaymentRequestDto.PaymentCheckRequest
    ): PaymentResponseDto.PaymentCheckResponse
    fun refundPaymentByToken(userId: Long): PaymentResponseDto.PaymentRefundResponse
    fun refundPayment(): Unit
    fun verifyPayment(userId: Long): PaymentResponseDto.PaymentRequestResponse
}
