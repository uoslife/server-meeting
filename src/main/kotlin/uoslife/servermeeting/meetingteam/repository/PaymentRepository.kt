package uoslife.servermeeting.meetingteam.repository

import java.util.UUID
import org.springframework.data.jpa.repository.JpaRepository
import uoslife.servermeeting.meetingteam.entity.Payment

interface PaymentRepository : JpaRepository<Payment, UUID> {}
