package uoslife.servermeeting.domain.meeting.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToOne
import uoslife.servermeeting.domain.meeting.domain.entity.enums.PaymentStatus
import uoslife.servermeeting.global.common.BaseEntity
import java.time.LocalDateTime

@Entity
class Payment(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Long? = null,

    @OneToOne(mappedBy = "payment", optional = false, orphanRemoval = true)
    var userTeam: UserTeam? = null,

    var date: LocalDateTime? = null,

    var amount: Int? = null,

    var address: String? = null,

    @Enumerated(EnumType.STRING)
    var status: PaymentStatus? = null,
) : BaseEntity()
