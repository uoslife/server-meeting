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
import uoslife.servermeeting.meetingteam.entity.enums.PaymentStatus
import uoslife.servermeeting.user.entity.User

@Entity
@Table(name = "payment")
class Payment(
    @Id @Column(name = "id", nullable = false) var id: UUID? = null,
    @OneToOne
    @JoinColumn(name = "payment_id", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    var user: User? = null,
    var mulNo: Int? = null,
    var var1: String? = null,
    var var2: String? = null,
    var price: Int? = null,
    var payDate: LocalDateTime? = null,
    @Enumerated(EnumType.STRING) var status: PaymentStatus? = null,
) : BaseEntity() {
    companion object {
        fun createPayment(
            user: User,
            var1: String,
            var2: String,
            price: Int,
        ): Payment {
            return Payment(
                user = user,
                mulNo = null,
                var1 = var1,
                var2 = var2,
                price = price,
                payDate = null,
                status = PaymentStatus.NONE
            )
        }
    }
}
