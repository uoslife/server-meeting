package uoslife.servermeeting.meetingteam.dto.response

import io.swagger.v3.oas.annotations.media.Schema

class PayappResponseDto {
    data class PayappRequestStatusResponse(
        @Schema(description = "결제 요청 성공/실패 여부") val state: Int,
        @Schema(description = "실패시 오류 문자열") val errorMessage: String,
        @Schema(description = "성공시 결제요청 번호") val mulNo: Int,
        @Schema(description = "결제창URL") val payUrl: String,
        @Schema(description = "결제창 QR URL") val qrUrl: String,
    )

    data class PayappCancelStatusResponse(
        @Schema(description = "성공: 1, 실패: 0") val state: Int,
        @Schema(description = "실패시 오류 문자열") val errorMessage: String,
        @Schema(description = "입금자명 (호출 파라메터)") val crDpname: String,
        @Schema(description = "결제취소 요청 구분 (호출 파라메터)") val partcancel: String,
        @Schema(description = "취소 반환금") val paybackprice: Int,
        @Schema(description = "결제취소요청 금액") val partprice: Int,
        @Schema(description = "결제취소요청 과세 공급가액") val cancelTaxable: Int,
        @Schema(description = "결제취소요청 부가세") val cancelVat: Int,
        @Schema(description = "결제취소요청 면세 공급가액") val cancelTaxfree: Int,
        @Schema(description = "취소 반환금 입금계좌") val paybackbank: String
    )

    data class PayappNotMatchingCancelResponse(
        @Schema(description = "환불 성공 수") val successCount: Int,
        @Schema(description = "환불 실패 수") val failedCount: Int,
        @Schema(description = "환불 정보")
        val payappCancelStatusResponseList: List<PayappCancelStatusResponse>,
    )
}
