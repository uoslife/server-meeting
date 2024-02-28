package uoslife.servermeeting.meetingteam.service

import java.util.*
import uoslife.servermeeting.meetingteam.dto.request.PortOneRequestDto
import uoslife.servermeeting.meetingteam.dto.response.PortOneResponseDto

interface PaymentService {
    fun requestPayment(
        userUUID: UUID,
        portOneRequestPaymentRequest: PortOneRequestDto.PortOneRequestPaymentRequest
    ): PortOneResponseDto.PortOneRequestPaymentResponse
    fun checkPayment()
    fun refundPaymentById()
    fun refundPayment()
}
