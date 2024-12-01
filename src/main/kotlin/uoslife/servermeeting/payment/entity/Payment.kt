package uoslife.servermeeting.payment.entity

import jakarta.persistence.*
import java.util.UUID
import uoslife.servermeeting.global.common.BaseEntity
import uoslife.servermeeting.meetingteam.entity.MeetingTeam
import uoslife.servermeeting.meetingteam.entity.enums.TeamType
import uoslife.servermeeting.payment.entity.enums.PayMethod
import uoslife.servermeeting.payment.entity.enums.PaymentGateway
import uoslife.servermeeting.payment.entity.enums.PaymentStatus
import uoslife.servermeeting.user.entity.User

@Entity
@Table(name = "payment")
class Payment(
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,
    @Enumerated(EnumType.STRING) var pg: PaymentGateway = PaymentGateway.WELCOME_PAYMENTS,
    @Enumerated(EnumType.STRING) var teamType: TeamType,
    @Enumerated(EnumType.STRING) var payMethod: PayMethod = PayMethod.CARD,
    var merchantUid: String,
    var price: Int,
    var impUid: String? = null,
    @Enumerated(EnumType.STRING) var status: PaymentStatus = PaymentStatus.PENDING,
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id") var user: User? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meetingTeam_id")
    var meetingTeam: MeetingTeam? = null,
) : BaseEntity() {
    companion object {
        fun createPayment(
            merchantUid: String,
            price: Int,
            status: PaymentStatus,
            meetingTeam: MeetingTeam,
            teamType: TeamType,
            user: User,
        ): Payment {
            return Payment(
                merchantUid = merchantUid,
                price = price,
                impUid = null,
                status = status,
                meetingTeam = meetingTeam,
                teamType = teamType,
                user = user
            )
        }
    }
    fun updatePayment(impUid: String, status: PaymentStatus) {
        this.impUid = impUid
        this.status = status
    }

    fun softDelete() {
        user = null
        status = PaymentStatus.USER_DELETED
    }

    fun removeMeetingTeam() {
        meetingTeam = null
        status = PaymentStatus.TEAM_DELETED
    }

    fun isSuccess(): Boolean {
        return status == PaymentStatus.SUCCESS
    }
}
