package uoslife.servermeeting.meetingteam.repository

import java.util.UUID
import org.springframework.data.jpa.repository.JpaRepository
import uoslife.servermeeting.meetingteam.entity.MeetingTeam
import uoslife.servermeeting.meetingteam.entity.Payment
import uoslife.servermeeting.meetingteam.entity.enums.PaymentStatus

interface PaymentRepository : JpaRepository<Payment, UUID> {
    fun findByStatus(status: PaymentStatus): List<Payment>
    fun findByMerchantUid(merchantUid: String): Payment?
    fun findByMeetingTeamAndStatus(meetingTeam: MeetingTeam, status: PaymentStatus): Payment?
}
