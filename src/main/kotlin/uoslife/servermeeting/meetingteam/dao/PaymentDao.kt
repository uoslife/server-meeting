package uoslife.servermeeting.meetingteam.dao

import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.transaction.Transactional
import org.springframework.stereotype.Repository
import uoslife.servermeeting.meetingteam.dto.response.PayappResponseDto
import uoslife.servermeeting.meetingteam.entity.Payment
import uoslife.servermeeting.meetingteam.entity.QPayment
import uoslife.servermeeting.meetingteam.entity.enums.PaymentStatus

@Repository
@Transactional
class PaymentDao(private val queryFactory: JPAQueryFactory) {
    fun updatePayment(
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
}
