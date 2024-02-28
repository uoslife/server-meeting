package uoslife.servermeeting.meetingteam.repository

import java.util.UUID
import org.springframework.data.jpa.repository.JpaRepository
import uoslife.servermeeting.meetingteam.entity.Payment
import uoslife.servermeeting.user.entity.User

interface PaymentRepository : JpaRepository<Payment, UUID> {
    fun findByUser(user: User): Payment?
}
