package uoslife.servermeeting.payment.dto.response

class PortOneResponseDto {
    data class AccessTokenResponse(
        var code: Int?,
        var message: String?,
        var response: AuthAnnotation?
    )

    data class AuthAnnotation(
        var access_token: String,
        var expired_at: Int,
        var now: Int,
    )

    data class SingleHistoryResponse(
        var code: Int?,
        var message: String?,
        var response: PaymentAnnotation?
    ) {
        fun isPaid(): Boolean {
            return response?.status == "paid"
        }
    }

    data class PaymentAnnotation(
        var imp_uid: String,
        var merchant_uid: String,
        var pay_method: String?,
        var channel: String?,
        var pg_provider: String?,
        var emb_pg_provider: String?,
        var pg_tid: String?,
        var pg_id: String?,
        var escrow: Boolean?,
        var apply_num: String?,
        var bank_code: String?,
        var bank_name: String?,
        var card_code: String?,
        var card_name: String?,
        var card_quota: Int?,
        var card_number: String?,
        var card_type: Int?,
        var vbank_code: String?,
        var vbank_name: String?,
        var vbank_num: String?,
        var vbank_holder: String?,
        var vbank_date: Int?,
        var vbank_issued_at: Int?,
        var name: String?,
        var amount: Double,
        var cancel_amount: Double,
        var currency: String,
        var buyer_name: String?,
        var buyer_email: String?,
        var buyer_tel: String?,
        var buyer_addr: String?,
        var buyer_postcode: String?,
        var custom_data: String?,
        var user_agent: String?,
        var status: String,
        var started_at: Int?,
        var paid_at: Int?,
        var failed_at: Int?,
        var cancelled_at: Int?,
        var fail_reason: String?,
        var cancel_reason: String?,
        var receipt_url: String?,
        var cancel_history: Array<PaymentCancelAnnotation>?,
        var cancel_receipt_urls: List<String>?,
        var cash_receipt_issued: Boolean?,
        var customer_uid: String?,
        var customer_uid_usage: String?,
    )

    data class PaymentCancelAnnotation(
        var pg_tid: String,
        var amount: Number,
        var cancelled_at: Int,
        var reason: String,
        var cancellation_id: String,
        var receipt_url: String?
    )

    data class RefundResponse(
        var code: Int?,
        var message: String?,
        var response: PaymentAnnotation?
    )
}
