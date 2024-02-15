package uoslife.servermeeting.meetingteam.service.impl

import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import uoslife.servermeeting.meetingteam.service.PaymentService

@Service
@Qualifier("PayappService")
class PayappService : PaymentService {

    @Transactional
    override fun spendPayment(): Unit {
        TODO("결제 요청 api 구현")
    }

    override fun cancelPaymentRequest(): Unit {
        TODO("모집 기간 중 결제 취소 api 구현(결제승인 후 5일 이전)")
        // 결제 취소 기능 없으면 필요 x
    }

    @Transactional
    override fun cancelPayment(): Unit {
        TODO("매칭이 안됐을 경우 결제 취소 api 구현(부분 취소)")
    }

}
