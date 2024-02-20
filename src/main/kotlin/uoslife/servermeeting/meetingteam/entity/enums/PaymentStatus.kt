package uoslife.servermeeting.meetingteam.entity.enums

enum class PaymentStatus {
    NONE,
    REQUEST, // 요청
    COMPLETE_PAYMENT, // 결제완료
    CANCEL_REQUEST, // 요청취소
    CANCEL_ACCEPTANCE, // 승인취소
    WAITING_PAYMENT, // 결제대기
    CANCEL_PARTIAL_REFUND, // 부분취소
    FAILED_REFUND // 환불실패
}
