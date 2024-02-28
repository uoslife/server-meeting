package uoslife.servermeeting.meetingteam.service

interface PaymentService {
    fun requestPayment()
    fun checkPayment()
    fun refundPaymentById()
    fun refundPayment()
}
