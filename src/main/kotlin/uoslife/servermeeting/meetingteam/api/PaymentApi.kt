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
import org.springframework.web.bind.annotation.GetMapping
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

    @Operation(
        summary = "결제 요청 API",
        description = "결제를 요청하여 주문 번호를 반환합니다. 유저의 결제정보가 이미 존재하면 존재하는 결제정보를 반환합니다."
    )
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
                    description = "요청 값에 문제가 있음",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            name = "U02",
                                            description = "해당 유저 정보 없음",
                                            value =
                                                "{message: User is not Found., status: 400, code: U02}"
                                        ),
                                        ExampleObject(
                                            name = "M06",
                                            description = "유저가 일치하는 팀 정보 없음",
                                            value =
                                                "{message: Meeting Team is not Found., status: 400, code: M06}"
                                        ),
                                        ExampleObject(
                                            name = "U06",
                                            description = "유저의 전화번호 없음(결제 불가)",
                                            value =
                                                "{message: Phone Number is not found, status: 400, code: U06}"
                                        ),
                                        ExampleObject(
                                            name = "P04",
                                            description = "유저가 이미 결제함",
                                            value =
                                                "{message: User already have Payment., status: 400, code: P04}"
                                        )]
                            )]
                ),
                ApiResponse(
                    responseCode = "401",
                    description = "부적절한 토큰 정보",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            value =
                                                "{message: Token is not valid., status: 401, code: T01}"
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
        val userId = userDetails.username.toLong()

        return ResponseEntity.status(HttpStatus.OK)
            .body(paymentService.requestPayment(userId, paymentRequestPaymentRequest))
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
                    description = "요청 값에 문제가 있음",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            name = "U02",
                                            description = "해당 유저 정보 없음",
                                            value =
                                                "{message: User is not Found., status: 400, code: U02}"
                                        ),
                                        ExampleObject(
                                            name = "P01",
                                            description = "결제 정보 없음",
                                            value =
                                                "{message: Payment is not Found., status: 400, code: P01}"
                                        )]
                            )]
                ),
                ApiResponse(
                    responseCode = "401",
                    description = "부적절한 토큰 정보",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            value =
                                                "{message: Token is not valid., status: 401, code: T01}"
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
        val userId = userDetails.username.toLong()

        return ResponseEntity.status(HttpStatus.OK)
            .body(paymentService.checkPayment(userId, paymentCheckRequest))
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
                    description = "요청 값에 문제가 있음",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            name = "U02",
                                            description = "해당 유저 정보 없음",
                                            value =
                                                "{message: User is not Found., status: 400, code: U02}"
                                        ),
                                        ExampleObject(
                                            name = "P01",
                                            description = "결제 정보 없음",
                                            value =
                                                "{message: Payment is not Found., status: 400, code: P01}"
                                        ),
                                        ExampleObject(
                                            name = "P03",
                                            description = "결제 정보가 부적절함(결제 상태가 아님)",
                                            value =
                                                "{message: Payment is Invalid., status: 400, code: P03}"
                                        )]
                            )]
                ),
                ApiResponse(
                    responseCode = "401",
                    description = "부적절한 토큰 정보",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            value =
                                                "{message: Token is not valid., status: 401, code: T01}"
                                        )]
                            )]
                ),
            ]
    )
    @PostMapping("/refund")
    fun refundPaymentByToken(
        @AuthenticationPrincipal userDetails: UserDetails,
    ): ResponseEntity<PaymentResponseDto.PaymentRefundResponse> {
        val userId = userDetails.username.toLong()

        return ResponseEntity.status(HttpStatus.OK)
            .body(paymentService.refundPaymentByToken(userId))
    }

    @Operation(summary = "매칭 안된 유저 결제 취소 API", description = "매칭이 되지않은 모든 유저에 대해 환불합니다")
    @ApiResponses(
        value =
            [
                ApiResponse(
                    responseCode = "204",
                    description = "반환값 없음",
                    content = [Content(schema = Schema(implementation = Unit::class))]
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
                    responseCode = "401",
                    description = "부적절한 토큰 정보",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            value =
                                                "{message: Token is not valid., status: 401, code: T01}"
                                        )]
                            )]
                ),
            ]
    )
    @PostMapping("/refund/match")
    fun refundPayment(): ResponseEntity<PaymentResponseDto.NotMatchedPaymentRefundResponse> {
        paymentService.refundPayment()
        return ResponseEntity.status(HttpStatus.OK).body(paymentService.refundPayment())
    }

    @Operation(summary = "결제 여부 확인 API", description = "결제 여부를 확인할 수 있습니다")
    @ApiResponses(
        value =
            [
                ApiResponse(
                    responseCode = "200",
                    description = "결제 요청은 했지만 결제 검증안됨",
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
                    description = "적절한 정보 없음",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            name = "U02",
                                            description = "해당 유저 정보 없음",
                                            value =
                                                "{message: User is not Found., status: 400, code: U02}"
                                        ),
                                        ExampleObject(
                                            name = "U02",
                                            description = "미팅팀 정보 없음",
                                            value =
                                                "{message: User is not Found., status: 400, code: M06}"
                                        ),
                                        ExampleObject(
                                            name = "M06",
                                            description = "유저 전화번호 정보 없음",
                                            value =
                                                "{message: Phone Number is not found., status: 400, code: U06}"
                                        ),
                                        ExampleObject(
                                            name = "P01",
                                            description = "결제 정보 없음",
                                            value =
                                                "{message: Payment is not Found., status: 400, code: P01}"
                                        ),
                                        ExampleObject(
                                            name = "P04",
                                            description = "이미 결제 완료함",
                                            value =
                                                "{message: User already have Payment., status: 400, code: P04}"
                                        )]
                            )]
                ),
                ApiResponse(
                    responseCode = "401",
                    description = "부적절한 토큰 정보",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            value =
                                                "{message: Token is not valid., status: 401, code: T01}"
                                        )]
                            )]
                ),
            ]
    )
    @GetMapping("/verify")
    fun verifyPayment(
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<PaymentResponseDto.PaymentRequestResponse> {
        val userId = userDetails.username.toLong()

        return ResponseEntity.status(HttpStatus.OK).body(paymentService.verifyPayment(userId))
    }
}
