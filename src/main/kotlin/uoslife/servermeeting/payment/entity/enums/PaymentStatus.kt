package uoslife.servermeeting.payment.entity.enums

enum class PaymentStatus {
    PENDING,
    REQUEST,
    SUCCESS,
    FAILED,
    REFUND_FAILED,
    REFUND,
    USER_DELETED,
    TEAM_DELETED,
}
