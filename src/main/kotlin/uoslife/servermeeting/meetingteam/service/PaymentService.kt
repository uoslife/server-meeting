package uoslife.servermeeting.meetingteam.service


interface PaymentService {
    fun spendPayment(): Unit
    fun cancelPaymentRequest(): Unit
    fun cancelPayment(): Unit
}
