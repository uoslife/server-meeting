package uoslife.servermeeting.global.common

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "tos")
class Tos(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long? = null,
    var privatePolicy: Boolean? = null,
    var termsOfUse: Boolean? = null,
    var notification: Boolean? = null,
    var marketing: Boolean? = null,
) : BaseEntity()
