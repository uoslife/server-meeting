package uoslife.servermeeting.entities

import jakarta.persistence.*



@Entity
@Table(name = "payments")
class Payment(
    @Id
    @Column(name = "payment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val paymentId: Long,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(name = "amount", nullable = false)
    val amount: Long,

    @Column(name = "payment_identifier")
    val paymentIdentifier: String,

    @Column(name = "payment_status")
    @Enumerated(EnumType.STRING)
    val paymentStatus: PaymentStatus = PaymentStatus.READY,

    @Column(name = "refund_account_number")
    val refundAccountNumber: String? = null,

    @Column(name = "refund_account_bank")
    val refundAccountBank: String? = null,

    @Column(name = "refund_need")
    val refundNeed: Boolean = false,

    @Column(name = "admin_memo")
    val adminMemo: String? = null,

    @Column(name = "admin_check_status")
    val adminCheckStatus: String? = null,

    @Column(name = "error_status")
    val errorStatus: String? = null
) : BaseEntity() {}