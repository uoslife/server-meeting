package uoslife.servermeeting.meetingteam.repository

import java.util.UUID
import org.springframework.data.jpa.repository.JpaRepository
import uoslife.servermeeting.meetingteam.entity.Payment
import uoslife.servermeeting.meetingteam.entity.enums.PaymentStatus
import uoslife.servermeeting.user.entity.User

interface PaymentRepository : JpaRepository<Payment, UUID> {
    fun findByUser(user: User): Payment?
    fun existsByUser(user: User): Boolean
    fun deleteByUser(user: User): Long
    fun findByStatus(status: PaymentStatus): List<Payment>
    fun findByMarchantUid(marchantUid: String): Payment?
}
