package uoslife.servermeeting.meetingteam.dto.response

class PayappResponseDto {
    data class PayappRequestStatusResponse(
        val state: Int,
        val errorMessage: String,
        val mulNo: Int,
        val payUrl: String,
        val qrUrl: String,
    )

    data class PayappCancelStatusResponse(
        val state: Int,
        val errorMessage: String,
        val crDpname: String,
        val partcancel: String,
        val paybackprice: Int,
        val partprice: Int,
        val cancelTaxable: Int,
        val cancelVat: Int,
        val cancelTaxfree: Int,
        val paybackbank: String,
    )

    data class PayappNotMatchingCancelResponse(
        val successCount: Int,
        val failedCount: Int,
        val payappCancelStatusResponseList: List<PayappCancelStatusResponse>,
    )
}
