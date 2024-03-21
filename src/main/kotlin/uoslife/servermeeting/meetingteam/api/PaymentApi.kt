package uoslife.servermeeting.meetingteam.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import java.util.*
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import uoslife.servermeeting.global.error.ErrorResponse
import uoslife.servermeeting.meetingteam.dto.request.PaymentRequestDto
import uoslife.servermeeting.meetingteam.dto.response.PaymentResponseDto
import uoslife.servermeeting.meetingteam.service.PaymentService

@RestController
@RequestMapping("/api/payment")
@Tag(name = "Payment", description = "결제 API")
class PaymentApi(@Qualifier("PortOneService") private val paymentService: PaymentService) {

    @Operation(summary = "결제 요청 API", description = "결제를 요청하여 주문 번호를 반환합니다.")
    @ApiResponses(
        value =
            [
                ApiResponse(
                    responseCode = "200",
                    description = "marchantUid 정상 반환",
                    content =
                        [
                            Content(
                                schema =
                                    Schema(
                                        implementation =
                                            PaymentResponseDto.PaymentRequestResponse::class
                                    )
                            )]
                ),
                ApiResponse(
                    responseCode = "400",
                    description = "해당 유저 정보 없음",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            value =
                                                "{message: User is not Found., status: 400, code: U02}"
                                        )]
                            )]
                ),
                ApiResponse(
                    responseCode = "400",
                    description = "유저가 일치하는 팀 정보 없음",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            value =
                                                "{message: Meeting Team is not Found., status: 400, code: M06}"
                                        )]
                            )]
                ),
                ApiResponse(
                    responseCode = "400",
                    description = "유저의 전화번호 없음(결제 불가)",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            value =
                                                "{message: Phone Number is not found, status: 400, code: U06}"
                                        )]
                            )]
                ),
                ApiResponse(
                    responseCode = "400",
                    description = "유저가 이미 결제함",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            value =
                                                "{message: User already have Payment., status: 400, code: P04}"
                                        )]
                            )]
                ),
            ]
    )
    @PostMapping("/request")
    fun requestPayment(
        @AuthenticationPrincipal userDetails: UserDetails,
        @RequestBody paymentRequestPaymentRequest: PaymentRequestDto.PaymentRequestRequest
    ): ResponseEntity<PaymentResponseDto.PaymentRequestResponse> {
        val userUUID = UUID.fromString(userDetails.username)

        return ResponseEntity.status(HttpStatus.OK)
            .body(paymentService.requestPayment(userUUID, paymentRequestPaymentRequest))
    }

    @Operation(summary = "결제 검증 API", description = "결제가 되었는지 검증합니다.")
    @ApiResponses(
        value =
            [
                ApiResponse(
                    responseCode = "200",
                    description = "결제 완료",
                    content =
                        [
                            Content(
                                schema =
                                    Schema(
                                        implementation =
                                            PaymentResponseDto.PaymentCheckResponse::class
                                    )
                            )]
                ),
                ApiResponse(
                    responseCode = "400",
                    description = "해당 유저 정보 없음",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            value =
                                                "{message: User is not Found., status: 400, code: U02}"
                                        )]
                            )]
                ),
                ApiResponse(
                    responseCode = "400",
                    description = "결제 정보 없음",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            value =
                                                "{message: Payment is not Found., status: 400, code: P01}"
                                        )]
                            )]
                ),
            ]
    )
    @PostMapping("/check")
    fun checkPayment(
        @AuthenticationPrincipal userDetails: UserDetails,
        @RequestBody paymentCheckRequest: PaymentRequestDto.PaymentCheckRequest
    ): ResponseEntity<PaymentResponseDto.PaymentCheckResponse> {
        val userUUID = UUID.fromString(userDetails.username)

        return ResponseEntity.status(HttpStatus.OK)
            .body(paymentService.checkPayment(userUUID, paymentCheckRequest))
    }

    @Operation(summary = "결제 취소 API", description = "이메일을 입력하면 특정한 유저에게 환불해줍니다.")
    @ApiResponses(
        value =
            [
                ApiResponse(
                    responseCode = "200",
                    description = "결제 환불 완료",
                    content =
                        [
                            Content(
                                schema =
                                    Schema(
                                        implementation =
                                            PaymentResponseDto.PaymentRefundResponse::class
                                    )
                            )]
                ),
                ApiResponse(
                    responseCode = "400",
                    description = "해당 유저 정보 없음",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            value =
                                                "{message: User is not Found., status: 400, code: U02}"
                                        )]
                            )]
                ),
                ApiResponse(
                    responseCode = "400",
                    description = "결제 정보 없음",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            value =
                                                "{message: Payment is not Found., status: 400, code: P01}"
                                        )]
                            )]
                ),
                ApiResponse(
                    responseCode = "400",
                    description = "결제 정보가 부적절함(결제 상태가 아님)",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            value =
                                                "{message: Payment is Invalid., status: 400, code: P03}"
                                        )]
                            )]
                ),
            ]
    )
    @PostMapping("/refund")
    fun refundPaymentByPhoneNumber(
        @RequestBody paymentRefundRequest: PaymentRequestDto.PaymentRefundRequest
    ): ResponseEntity<PaymentResponseDto.PaymentRefundResponse> {
        return ResponseEntity.status(HttpStatus.OK)
            .body(paymentService.refundPaymentByPhoneNumber(paymentRefundRequest.phoneNumber))
    }

    @Operation(summary = "매칭 안된 유저 결제 취소 API", description = "매칭이 되지않은 모든 유저에 대해 환불합니다")
    @ApiResponses(
        value =
            [
                ApiResponse(
                    responseCode = "200",
                    description = "환볼 성공/실패 수, 각 환불정보 제공료",
                    content =
                        [
                            Content(
                                schema =
                                    Schema(
                                        implementation =
                                            PaymentResponseDto
                                                .PaymentNotMatchingRefundResponse::class
                                    )
                            )]
                ),
                ApiResponse(
                    responseCode = "400",
                    description = "결제 정보 없음",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            value =
                                                "{message: Payment is not Found., status: 400, code: P01}"
                                        )]
                            )]
                ),
            ]
    )
    @PostMapping("/refund/match")
    fun refundPayment(): ResponseEntity<PaymentResponseDto.PaymentNotMatchingRefundResponse> {
        return ResponseEntity.status(HttpStatus.OK).body(paymentService.refundPayment())
    }
}
