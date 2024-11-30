package uoslife.servermeeting.admin.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import uoslife.servermeeting.admin.dto.request.ResetEmailRequest
import uoslife.servermeeting.admin.service.AdminEmailVerificationService

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin", description = "어드민 API")
class AdminApi(private val adminEmailVerificationService: AdminEmailVerificationService) {
    @Operation(summary = "이메일 발송 횟수 초기화", description = "특정 이메일의 일일 발송 횟수를 초기화합니다.")
    @PostMapping("/verification/send-email/reset")
    fun resetEmailSendCount(@RequestBody request: ResetEmailRequest): ResponseEntity<Unit> {
        adminEmailVerificationService.resetEmailSendCount(request.email)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }
}
