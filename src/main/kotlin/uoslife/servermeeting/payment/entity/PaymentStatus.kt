package uoslife.servermeeting.payment.entity

enum class PaymentStatus {
    WAITING, READY, PROCESSING, COMPLETE, CANCEL, ERROR, REFUND,
}