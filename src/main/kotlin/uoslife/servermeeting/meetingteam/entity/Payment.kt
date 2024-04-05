package uoslife.servermeeting.meetingteam.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID
import uoslife.servermeeting.global.common.BaseEntity
import uoslife.servermeeting.meetingteam.entity.enums.PayMethod
import uoslife.servermeeting.meetingteam.entity.enums.PaymentGateway
import uoslife.servermeeting.meetingteam.entity.enums.PaymentStatus
import uoslife.servermeeting.user.entity.User

@Entity
@Table(name = "payment")
class Payment(
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,
    @OneToOne @JoinColumn(name = "user_id") var user: User? = null,
    @Enumerated(EnumType.STRING) var pg: PaymentGateway? = null,
    @Enumerated(EnumType.STRING) var payMethod: PayMethod? = null,
    var marchantUid: String? = null,
    var price: Int? = null,
    var impUid: String? = null,
    var paidDate: LocalDateTime? = null,
    @Enumerated(EnumType.STRING) var status: PaymentStatus = PaymentStatus.NONE,
) : BaseEntity() {
    companion object {
        fun createPayment(
            user: User,
            pg: PaymentGateway,
            payMethod: PayMethod,
            marchantUid: String,
            price: Int,
            status: PaymentStatus,
        ): Payment {
            return Payment(
                user = user,
                pg = pg,
                payMethod = payMethod,
                marchantUid = marchantUid,
                price = price,
                impUid = null,
                paidDate = null,
                status = status,
            )
        }
    }
}
