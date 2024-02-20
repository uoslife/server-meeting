package uoslife.servermeeting.meetingteam.dao

import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.transaction.Transactional
import java.time.LocalDateTime
import org.springframework.stereotype.Repository
import uoslife.servermeeting.meetingteam.entity.Payment
import uoslife.servermeeting.meetingteam.entity.QPayment.*
import uoslife.servermeeting.meetingteam.entity.enums.PaymentStatus

@Repository
@Transactional
class PaymentDao(private val queryFactory: JPAQueryFactory) {
    fun updatePaymentStatus(updatePayment: Payment, paymentStatus: PaymentStatus) {
        queryFactory
            .update(payment)
            .where(payment.eq(updatePayment))
            .set(payment.status, paymentStatus)
            .execute()
    }
    fun updatePaymentMulNo(
        updatePayment: Payment,
        mulNo: Int,
    ) {
        queryFactory
            .update(payment)
            .where(payment.eq(updatePayment))
            .set(payment.mulNo, mulNo)
            .execute()
    }

    fun updatePaymentPayDate(updatePayment: Payment, localDateTime: LocalDateTime) {
        queryFactory
            .update(payment)
            .where(payment.eq(updatePayment))
            .set(payment.payDate, LocalDateTime.now())
            .execute()
    }

    fun selectPaymentByMulNoAndVar(mulNo: Int, identifier1: String, identifier2: String): Payment? {
        return queryFactory
            .select(payment)
            .from(payment)
            .where(
                payment.mulNo
                    .eq(mulNo)
                    .and(payment.identifier1.eq(identifier1))
                    .and(payment.identifier2.eq(identifier2))
            )
            .fetchOne()
    }
}
