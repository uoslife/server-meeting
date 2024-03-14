package uoslife.servermeeting.user.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import uoslife.servermeeting.global.common.BaseEntity

@Entity
@Table(name = "tos")
class Tos(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var privatePolicy: Boolean? = null,
    var termsOfUse: Boolean? = null,
    var marketing: Boolean? = null,
    var doNotShare: Boolean? = null,
) : BaseEntity()
