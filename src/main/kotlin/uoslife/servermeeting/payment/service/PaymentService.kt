package uoslife.servermeeting.payment.service

import uoslife.servermeeting.meetingteam.entity.enums.TeamType
import uoslife.servermeeting.payment.dto.request.PaymentRequestDto
import uoslife.servermeeting.payment.dto.response.PaymentResponseDto
import uoslife.servermeeting.user.entity.User

interface PaymentService {
    fun requestPayment(
        userId: Long,
        paymentRequestPaymentRequest: PaymentRequestDto.PaymentRequestRequest,
        teamType: TeamType,
    ): PaymentResponseDto.PaymentRequestResponse
    fun checkPayment(
        userId: Long,
        teamType: TeamType,
        paymentCheckRequest: PaymentRequestDto.PaymentCheckRequest,
    ): PaymentResponseDto.PaymentCheckResponse
    fun refundPaymentByToken(
        userId: Long,
        teamType: TeamType
    ): PaymentResponseDto.PaymentRefundResponse
    fun refundPayment(): PaymentResponseDto.NotMatchedPaymentRefundResponse
    fun verifyPayment(userId: Long, teamType: TeamType): PaymentResponseDto.PaymentRequestResponse
    fun deleteUserPayment(user: User)
    fun synchronizePayment(paymentWebhookResponse: PaymentResponseDto.PaymentWebhookResponse)
}
