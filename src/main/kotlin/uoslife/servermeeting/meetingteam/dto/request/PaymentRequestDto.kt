package uoslife.servermeeting.meetingteam.dto.request

import uoslife.servermeeting.meetingteam.entity.enums.PayMethod
import uoslife.servermeeting.meetingteam.entity.enums.PaymentGateway

class PaymentRequestDto {
    data class PaymentRequestRequest(
        var pg: PaymentGateway,
        var payMethod: PayMethod,
    )

    data class PaymentCheckRequest(
        var impUid: String,
    )
}
