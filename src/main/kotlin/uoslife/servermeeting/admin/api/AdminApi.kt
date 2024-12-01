package uoslife.servermeeting.admin.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import uoslife.servermeeting.admin.dto.request.DeleteUserRequest
import uoslife.servermeeting.admin.dto.request.ResetEmailRequest
import uoslife.servermeeting.admin.service.AdminService
import uoslife.servermeeting.global.config.SwaggerConfig
import uoslife.servermeeting.global.error.ErrorResponse

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin", description = "개발자 전용 API")
@SecurityRequirement(name = SwaggerConfig.API_KEY)
@ApiResponses(
    value =
        [
            ApiResponse(
                responseCode = "401",
                description = "API Key 인증 실패",
                content =
                    [
                        Content(
                            schema = Schema(implementation = ErrorResponse::class),
                            examples =
                                [
                                    ExampleObject(
                                        name = "API Key Missing",
                                        value =
                                            "{\"message\": \"API key not found\", \"status\": 401, \"code\": \"AD01\"}",
                                        description = "X-API-Key 헤더가 없는 경우"
                                    ),
                                    ExampleObject(
                                        name = "API Key Invalid",
                                        value =
                                            "{\"message\": \"Invalid API Key\", \"status\": 401, \"code\": \"AD02\"}",
                                        description = "잘못된 API Key인 경우"
                                    )]
                        )]
            )]
)
class AdminApi(private val adminService: AdminService) {
    @Operation(summary = "이메일 발송 횟수 초기화", description = "특정 이메일의 일일 발송 횟수를 초기화합니다.")
    @ApiResponses(
        value =
            [
                ApiResponse(
                    responseCode = "204",
                    description = "초기화 성공",
                    content = [Content(schema = Schema(implementation = Unit::class))]
                ),
            ]
    )
    @PostMapping("/verification/send-email/reset")
    fun resetEmailSendCount(@RequestBody request: ResetEmailRequest): ResponseEntity<Unit> {
        adminService.resetEmailSendCount(request.email)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }

    @Operation(summary = "특정 유저 삭제", description = "특정 유저의 계정과 관련 정보를 삭제합니다.")
    @ApiResponses(
        value =
            [
                ApiResponse(
                    responseCode = "204",
                    description = "유저 삭제 성공",
                    content = [Content(schema = Schema(implementation = Unit::class))]
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
                                                "{message: User is not Found., status:400, code: U02}"
                                        )]
                            )]
                )]
    )
    @DeleteMapping("/user")
    fun deleteUser(
        @RequestBody request: DeleteUserRequest,
        response: HttpServletResponse
    ): ResponseEntity<Unit> {
        adminService.deleteUserById(request.userId, response)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }
}
