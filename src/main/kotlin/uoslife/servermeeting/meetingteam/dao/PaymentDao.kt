package uoslife.servermeeting.meetingteam.dao

import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.transaction.Transactional
import org.springframework.stereotype.Repository
import uoslife.servermeeting.meetingteam.entity.Payment
import uoslife.servermeeting.meetingteam.entity.enums.PaymentStatus

@Repository
@Transactional
class PaymentDao(private val queryFactory: JPAQueryFactory) {
    fun updateOrCreatePayment(payment: Payment?, paymentStatus: PaymentStatus) {
        // 1. 결제 성공 && 이미 payment에 존재 경우 -> update(paid)
        // 2. 결제 성공 && payment 새로 생성 -> create(paid)
        // 3. 결제 실패 && 이미 payment에 존재 경우 -> update(failed)
        // 4. 결제 실패 && payment 새로 생성 -> create(failed)
        TODO("query 작성")
    }
}
