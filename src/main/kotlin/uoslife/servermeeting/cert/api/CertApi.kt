package uoslife.servermeeting.cert.api

import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import uoslife.servermeeting.cert.dto.request.CertifyRequest
import uoslife.servermeeting.cert.dto.request.VerifyCodeRequest
import uoslife.servermeeting.cert.dto.response.CertCheckResponse
import uoslife.servermeeting.cert.dto.response.SendMailResponse
import uoslife.servermeeting.cert.dto.response.VerifyCodeResponse
import uoslife.servermeeting.cert.service.CertService

@RestController
@RequestMapping("/api/cert")
class CertApi (
    private val certService: CertService
) {
    @Operation(summary = "대학 메일 인증 시작", description = "메일 인증을 위해 인증 코드를 내포한 메일을 대학 메일로 보냅니다.")
    @PostMapping
    fun sendMail(
        @RequestBody certifyRequest: CertifyRequest
    ): ResponseEntity<SendMailResponse>{
        val status: Boolean = certService.sendMail(certifyRequest)

        return ResponseEntity.ok()
            .body(SendMailResponse(status))
    }
}
