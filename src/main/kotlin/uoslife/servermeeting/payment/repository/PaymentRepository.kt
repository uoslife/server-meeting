package uoslife.servermeeting.payment.repository

import org.springframework.data.jpa.repository.JpaRepository
import uoslife.servermeeting.payment.entity.Payment

interface PaymentRepository : JpaRepository<Payment, Long> {

}