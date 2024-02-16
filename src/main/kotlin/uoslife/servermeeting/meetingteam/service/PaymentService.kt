package uoslife.servermeeting.meetingteam.service

import java.util.UUID
import uoslife.servermeeting.meetingteam.entity.enums.PaymentStatus

interface PaymentService {
    fun spendPayment(userUUID: UUID): PaymentStatus
    fun refundPaymentById(): Unit
    fun refundPayment(): Unit
}
