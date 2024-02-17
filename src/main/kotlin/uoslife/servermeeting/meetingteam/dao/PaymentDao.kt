package uoslife.servermeeting.meetingteam.dao

import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.transaction.Transactional
import java.time.LocalDateTime
import org.springframework.stereotype.Repository
import uoslife.servermeeting.meetingteam.dto.response.PayappResponseDto
import uoslife.servermeeting.meetingteam.entity.Payment
import uoslife.servermeeting.meetingteam.entity.QPayment
import uoslife.servermeeting.meetingteam.entity.enums.PaymentStatus

@Repository
@Transactional
class PaymentDao(private val queryFactory: JPAQueryFactory) {
    fun updatePaymentByRequest(
        updatePayment: Payment,
        payappRequestStatusResponse: PayappResponseDto.PayappRequestStatusResponse,
        paymentStatus: PaymentStatus
    ) {
        queryFactory
            .update(QPayment.payment)
            .where(QPayment.payment.eq(updatePayment))
            .set(QPayment.payment.status, paymentStatus)
            .set(QPayment.payment.mul_no, payappRequestStatusResponse.mulNo)
            .execute()
    }

    fun selectPaymentByMulNoAndVar(mulNo: Int, var1: String, var2: String): Payment? {
        return queryFactory
            .select(QPayment.payment)
            .from(QPayment.payment)
            .where(
                QPayment.payment.mul_no
                    .eq(mulNo)
                    .and(QPayment.payment.var1.eq(var1))
                    .and(QPayment.payment.var2.eq(var2))
            )
            .fetchOne()
    }

    fun updatePaymentByCheck(updatePayment: Payment, paymentStatus: PaymentStatus) {
        queryFactory
            .update(QPayment.payment)
            .where(QPayment.payment.eq(updatePayment))
            .set(QPayment.payment.status, paymentStatus)
            .set(QPayment.payment.payDate, LocalDateTime.now())
            .execute()
    }
}
