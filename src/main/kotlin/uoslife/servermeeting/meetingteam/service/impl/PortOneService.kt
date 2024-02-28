package uoslife.servermeeting.meetingteam.service.impl

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import uoslife.servermeeting.meetingteam.service.PaymentService

@Service
@Qualifier("PortOneService")
class PortOneService (

): PaymentService {
    override fun requestPayment() {
        TODO("결제 요청")
    }

    override fun checkPayment() {
        TODO("결제 검증")
    }

    override fun refundPaymentById() {
        TODO("특정 유저 결제 취소")
    }

    override fun refundPayment() {
        TODO("비매칭인원 전체 결제 취소")
    }
}
