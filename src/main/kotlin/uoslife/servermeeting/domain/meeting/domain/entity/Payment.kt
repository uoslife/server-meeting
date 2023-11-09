package uoslife.servermeeting.domain.meeting.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.MapsId
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import uoslife.servermeeting.domain.meeting.domain.entity.enums.PaymentStatus
import uoslife.servermeeting.global.common.BaseEntity
import java.time.LocalDateTime

@Entity
@Table(name = "payment")
class Payment(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Long? = null,

    @OneToOne
    @MapsId
    @JoinColumn(name = "payment_id")
    var userTeam: UserTeam? = null,

    var date: LocalDateTime? = null,

    var amount: Int? = null,

    var address: String? = null,

    @Enumerated(EnumType.STRING)
    var status: PaymentStatus? = null,
) : BaseEntity()
