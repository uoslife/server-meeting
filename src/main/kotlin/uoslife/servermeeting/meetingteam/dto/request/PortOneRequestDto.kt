package uoslife.servermeeting.meetingteam.dto.request

import uoslife.servermeeting.meetingteam.entity.enums.PayMethod
import uoslife.servermeeting.meetingteam.entity.enums.PaymentGateway

class PortOneRequestDto {
    data class PortOneRequestPaymentRequest(
        var pg: PaymentGateway,
        var payMethod: PayMethod,
    )
}
