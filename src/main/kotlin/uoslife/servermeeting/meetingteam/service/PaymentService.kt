package uoslife.servermeeting.meetingteam.service

import java.util.*
import uoslife.servermeeting.meetingteam.dto.request.PaymentRequestDto
import uoslife.servermeeting.meetingteam.dto.response.PaymentResponseDto

interface PaymentService {
    fun requestPayment(
        userUUID: UUID,
        paymentRequestPaymentRequest: PaymentRequestDto.PaymentRequestRequest
    ): PaymentResponseDto.PaymentRequestResponse
    fun checkPayment(
        userUUID: UUID,
        paymentCheckRequest: PaymentRequestDto.PaymentCheckRequest
    ): PaymentResponseDto.PaymentCheckResponse
    fun refundPaymentByPhoneNumber(phoneNumber: String): PaymentResponseDto.PaymentRefundResponse
    fun refundPayment(): PaymentResponseDto.PaymentNotMatchingRefundResponse
}
