package uoslife.servermeeting.meetingteam.entity

import jakarta.persistence.*
import java.util.UUID
import uoslife.servermeeting.global.common.BaseEntity
import uoslife.servermeeting.meetingteam.entity.enums.PayMethod
import uoslife.servermeeting.meetingteam.entity.enums.PaymentGateway
import uoslife.servermeeting.meetingteam.entity.enums.PaymentStatus

@Entity
@Table(name = "payment")
class Payment(
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,
    @Enumerated(EnumType.STRING) var pg: PaymentGateway = PaymentGateway.WELCOME_PAYMENTS,
    @Enumerated(EnumType.STRING) var payMethod: PayMethod = PayMethod.CARD,
    var merchantUid: String? = null,
    var price: Int? = null,
    var impUid: String? = null,
    @Enumerated(EnumType.STRING) var status: PaymentStatus = PaymentStatus.NONE,
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "payment")
    var singleMeetingTeam: SingleMeetingTeam? = null,
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "payment")
    var tripleMeetingTeam: TripleMeetingTeam? = null,
) : BaseEntity() {
    companion object {
        fun createPayment(
            merchantUid: String,
            price: Int,
            status: PaymentStatus,
        ): Payment {
            return Payment(
                merchantUid = merchantUid,
                price = price,
                impUid = null,
                status = status,
            )
        }
    }
    fun updatePayment(impUid: String, status: PaymentStatus) {
        this.impUid = impUid
        this.status = status
    }
}
