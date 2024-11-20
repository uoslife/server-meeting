package uoslife.servermeeting.payment.repository

import java.util.UUID
import org.springframework.data.jpa.repository.JpaRepository
import uoslife.servermeeting.meetingteam.entity.MeetingTeam
import uoslife.servermeeting.payment.entity.Payment
import uoslife.servermeeting.payment.entity.enums.PaymentStatus
import uoslife.servermeeting.user.entity.User

interface PaymentRepository : JpaRepository<Payment, UUID> {
    fun findByStatus(status: PaymentStatus): List<Payment>
    fun findByMerchantUid(merchantUid: String): Payment?
    fun findByMeetingTeamAndStatus(meetingTeam: MeetingTeam, status: PaymentStatus): Payment?
    fun findAllByUser(user: User): List<Payment>?
}
