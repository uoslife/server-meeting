package uoslife.servermeeting.meetingteam.entity.enums

enum class PaymentStatus {
    NONE,
    REQUEST,
    COMPLETE_PAYMENT,
    CANCEL_REQUEST,
    CANCEL_ACCEPTANCE,
    WAITING_PAYMENT,
    CANCEL_PARTIAL_REFUND,
    FAILED_REFUND
}
