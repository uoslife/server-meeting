package uoslife.servermeeting.payment.dto.request

class PortOneRequestDto {
    data class AccessTokenRequest(
        val imp_key: String,
        val imp_secret: String,
    )

    data class RefundRequest(
        val imp_uid: String?,
        val amount: Number?,
    )
}
