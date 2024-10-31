package uoslife.servermeeting.meetingteam.service

import uoslife.servermeeting.meetingteam.dto.request.PaymentRequestDto
import uoslife.servermeeting.meetingteam.dto.response.PaymentResponseDto
import uoslife.servermeeting.meetingteam.entity.enums.TeamType
import uoslife.servermeeting.user.entity.User

interface PaymentService {
    fun requestPayment(
        userId: Long,
        paymentRequestPaymentRequest: PaymentRequestDto.PaymentRequestRequest,
        teamType: TeamType
    ): PaymentResponseDto.PaymentRequestResponse
    fun checkPayment(
        userId: Long,
        paymentCheckRequest: PaymentRequestDto.PaymentCheckRequest
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
