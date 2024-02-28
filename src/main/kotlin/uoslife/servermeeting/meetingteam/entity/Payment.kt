package uoslife.servermeeting.meetingteam.entity

import jakarta.persistence.Column
import jakarta.persistence.ConstraintMode
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.ForeignKey
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
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
    @Id @Column(name = "id", nullable = false) var id: UUID? = null,
    @OneToOne
    @JoinColumn(name = "payment_id", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    var user: User? = null,
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
