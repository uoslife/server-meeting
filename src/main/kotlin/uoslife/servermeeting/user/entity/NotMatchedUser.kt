package uoslife.servermeeting.user.entity

import jakarta.persistence.*
import uoslife.servermeeting.meetingteam.entity.enums.PaymentStatus

@Entity
@Table(name = "not_matched_user")
class NotMatchedUser(
    @Id
    val id: Long,
    var kakaoTalkId: String = "",
    var name: String,
    var phoneNumber: String,
    var marchantUid: String,
    var impUid: String,
    var price: Int,
    @Enumerated(EnumType.STRING) var status: PaymentStatus = PaymentStatus.NONE
){
    override fun toString(): String {
        return "NotMatchedUser(id=$id, kakaoTalkId='$kakaoTalkId', name='$name', phoneNumber='$phoneNumber', marchantUid='$marchantUid', impUid='$impUid', price=$price, status=$status)"
    }
}
