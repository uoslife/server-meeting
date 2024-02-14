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
import uoslife.servermeeting.meetingteam.entity.enums.PaymentStatus
import uoslife.servermeeting.user.entity.User
import uoslife.servermeeting.global.common.BaseEntity

@Entity
@Table(name = "payment")
class Payment(
    @Id @Column(name = "id", nullable = false) var id: UUID? = null,
    @OneToOne
    @JoinColumn(name = "payment_id", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    var user: User? = null,
    var date: LocalDateTime? = null,
    var amount: Int? = null,
    var address: String? = null,
    var name: String? = null,
    var payedUserId: UUID? = null,
    @Enumerated(EnumType.STRING) var status: PaymentStatus? = null,
) : BaseEntity()
