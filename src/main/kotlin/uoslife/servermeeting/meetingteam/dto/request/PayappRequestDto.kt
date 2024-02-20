package uoslife.servermeeting.meetingteam.dto.request

import io.swagger.v3.oas.annotations.media.Schema

class PayappRequestDto {
    data class PayappCheckStatusRequest(
        @Schema(description = "판매자 회원 아이디") val userid: String,
        @Schema(description = "연동 KEY") val linkkey: String,
        @Schema(description = "연동 VALUE") val linkval: String,
        @Schema(description = "상품명") val goodname: String,
        @Schema(description = "결제요청 금액") val price: Int,
        @Schema(description = "수신 휴대폰번호") val recvphone: String,
        @Schema(description = "메모") val memo: String?,
        @Schema(description = "주소요청 (1:요청, 0:요청안함)") val reqaddr: Int,
        @Schema(description = "결제요청 일시") val reqdate: String,
        @Schema(description = "결제시 입력한 메모") val payMemo: String?,
        @Schema(description = "결제시 입력한 주소") val payAddr: String?,
        @Schema(description = "결제승인 일시") val payDate: String,
        @Schema(
            description =
                "결제수단 (1: 신용카드, 2: 휴대전화, 3: 해외결제, 4: 대면결제, " +
                    "6: 계좌이체, 7: 가상계좌, 15: 카카오페이, 16: 네이버페이, " +
                    "17: 등록결제, 21: 스마일페이, 23: 애플페이)"
        )
        val payType: Int,
        @Schema(
            description =
                "결제요청 상태 (1: 요청, 4: 결제완료, 8, 32: 요청취소, " + "9, 64: 승인취소, 10: 결제대기, 70, 71: 부분취소)"
        )
        val payState: Int,
        @Schema(description = "임의 사용 변수 1") val identifier1: String,
        @Schema(description = "임의 사용 변수 2") val identifier2: String,
        @Schema(description = "결제요청번호(결제취소시 사용함)") val mulNo: Int,
        @Schema(description = "결제페이지 주소") val payurl: String,
        @Schema(description = "신용카드 결제시 매출전표 URL") val csturl: String,
        @Schema(description = "통화 (krw: 원화, usd: 달러)") val currency: String,
        @Schema(description = "국제전화 국가번호") val vccode: String?,
        @Schema(description = "결제요청 연동 (0: 결제요청 연동, 1: 공통 통보 URL)") val feedbacktype: Int,
        @Schema(description = "원거래 결제요청번호 (부분취소일 경우 값이 있음)") val origMulNo: Int?,
        @Schema(description = "원거래 결제요청금액 (부분취소일 경우 값이 있음)") val origPrice: Int?,
        @Schema(description = "과세 공급가액") val amountTaxable: Int?,
        @Schema(description = "면세 공급가액") val amountTaxfree: Int?,
        @Schema(description = "부가세") val amountVat: Int?,
        @Schema(description = "구매자 아이디") val buyerid: String?,
        @Schema(description = "취소일시") val canceldate: String?,
        @Schema(description = "취소메모") val cancelmemo: String?,
        @Schema(description = "정기결제 등록번호 (정기결제일 경우)") val rebillNo: String?,
        @Schema(description = "신용카드명 (신용카드 결제일 경우)") val cardName: String?,
        @Schema(description = "승인번호 (신용카드 결제일 경우)") val payauthcode: String?,
        @Schema(description = "할부개월 (신용카드 결제일 경우)") val cardQuota: String?,
        @Schema(description = "카드번호 (신용카드 결제일 경우)") val cardNum: String?,
        @Schema(description = "은행명 (가상계좌 결제일 경우)") val vbank: String?,
        @Schema(description = "입금계좌번호 (가상계좌 결제일 경우)") val vbankno: String?,
        @Schema(description = "네이버 포인트 결제시 금액 (네이버페이 결제시만 제공)") val naverpoint: Int?,
        @Schema(description = "네이버결제 구분 방법 (네이버페이 결제시만 제공)") val naverpay: String?,
        @Schema(description = "스마일캐시 이용금액 (스마일페이 결제시만 제공)") val smilepaySmilecash: Int?,
        @Schema(description = "스마일페이 프로모션 이용금액 (스마일페이 결제시만 제공)") val smilepayDiscountamt: Int?
    )
}
