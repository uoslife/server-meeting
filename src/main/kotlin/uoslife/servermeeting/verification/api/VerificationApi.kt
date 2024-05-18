// package uoslife.servermeeting.verification.api
//
// import io.swagger.v3.oas.annotations.Operation
// import io.swagger.v3.oas.annotations.media.Content
// import io.swagger.v3.oas.annotations.media.ExampleObject
// import io.swagger.v3.oas.annotations.media.Schema
// import io.swagger.v3.oas.annotations.responses.ApiResponse
// import io.swagger.v3.oas.annotations.responses.ApiResponses
// import io.swagger.v3.oas.annotations.tags.Tag
// import jakarta.servlet.http.HttpServletResponse
// import jakarta.validation.Valid
// import org.springframework.http.HttpStatus
// import org.springframework.http.ResponseEntity
// import org.springframework.web.bind.annotation.PostMapping
// import org.springframework.web.bind.annotation.RequestBody
// import org.springframework.web.bind.annotation.RequestMapping
// import org.springframework.web.bind.annotation.RestController
// import uoslife.servermeeting.global.auth.dto.response.AccessTokenResponse
// import uoslife.servermeeting.global.auth.dto.response.TokenResponse
// import uoslife.servermeeting.global.auth.jwt.TokenProvider
// import uoslife.servermeeting.global.error.ErrorResponse
// import uoslife.servermeeting.global.util.CookieUtil
// import uoslife.servermeeting.verification.dto.request.VerificationCodeCheckRequest
// import uoslife.servermeeting.verification.dto.request.VerificationCodeSendRequest
// import uoslife.servermeeting.verification.dto.response.*
// import uoslife.servermeeting.verification.service.VerificationService
//
// @RestController
// @RequestMapping("/api/verification")
// @Tag(name = "Verification", description = "이메일 인증 API")
// class VerificationApi(
//    private val verificationService: VerificationService,
//    private val tokenProvider: TokenProvider,
//    private val cookieUtil: CookieUtil,
// ) {
//    @Operation(
//        summary = "메일 인증 코드 전송(계정 서비스 생성으로 미사용)",
//        description = "메일 인증을 위해 인증 코드를 내포한 메일을 대학 메일로 보냅니다."
//    )
//    @ApiResponses(
//        value =
//            [
//                ApiResponse(
//                    responseCode = "204",
//                    description = "메일로 인증 코드 전송 성공",
//                    content =
//                        [
//                            Content(
//                                schema =
//                                    Schema(implementation = VerificationCodeSendResponse::class)
//                            )]
//                ),
//                ApiResponse(
//                    responseCode = "500",
//                    description = "이메일 전송 실패",
//                    content =
//                        [
//                            Content(
//                                schema = Schema(implementation = ErrorResponse::class),
//                                examples =
//                                    [
//                                        ExampleObject(
//                                            value =
//                                                "{message: Email send is failed., status: 500,
// code: E01}"
//                                        )]
//                            )]
//                )]
//    )
//    @PostMapping("/send")
//    fun sendMail(
//        @RequestBody @Valid verificationCodeSendRequest: VerificationCodeSendRequest
//    ): ResponseEntity<Unit> {
//        verificationService.sendMail(verificationCodeSendRequest)
//
//        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
//    }
//
//    @Operation(
//        summary = "인증 코드 확인(계정 서비스 생성으로 미사용)",
//        description =
//            "verification 테이블의 인증 코드와 사용자가 입력한 인증 코드를 비교합니다. 일치하면 회원가입(이미 존재하면 로그인) 후 토큰을 발급합니다.
// (인증코드 000000이면 확인 안 하고 토큰 발급)"
//    )
//    @ApiResponses(
//        value =
//            [
//                ApiResponse(
//                    responseCode = "200",
//                    description = "메일을 통한 본인 인증 성공, AccessToken 반환",
//                    content =
//                        [Content(schema = Schema(implementation = AccessTokenResponse::class))]
//                ),
//                ApiResponse(
//                    responseCode = "400",
//                    description = "인증 코드 불일치",
//                    content =
//                        [
//                            Content(
//                                schema = Schema(implementation = ErrorResponse::class),
//                                examples =
//                                    [
//                                        ExampleObject(
//                                            value =
//                                                "{message: Verification code does not match.,
// status: 400, code: V01}"
//                                        )]
//                            )]
//                )]
//    )
//    @PostMapping("/verify")
//    fun verifyCode(
//        @RequestBody @Valid verificationCodeCheckRequest: VerificationCodeCheckRequest,
//        response: HttpServletResponse
//    ): ResponseEntity<AccessTokenResponse> {
//        val tokenResponse: TokenResponse =
//            verificationService.verifyVerificationCode(verificationCodeCheckRequest)
//        cookieUtil.setCookieWithRefreshToken(response, tokenResponse.refreshToken)
//
//        return ResponseEntity.ok()
//            .body(AccessTokenResponse(accessToken = tokenResponse.accessToken))
//    }
// }
