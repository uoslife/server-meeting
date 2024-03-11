package uoslife.servermeeting.meetingteam.dto.request

import uoslife.servermeeting.meetingteam.entity.enums.PayMethod
import uoslife.servermeeting.meetingteam.entity.enums.PaymentGateway

class PaymentRequestDto {
    data class PaymentRequestRequest(
        val pg: PaymentGateway,
        val payMethod: PayMethod,
    )

    data class PaymentCheckRequest(
        val impUid: String,
    )

    data class PaymentRefundRequest(
        val email: String,
    )
}
