import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import uoslife.servermeeting.global.exception.ErrorCode
import java.time.LocalDateTime

data class ErrorResponse(
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val status: Int,
    val error: String,
    val code: String,
    val message: String
) {
    companion object {
        fun toResponseEntity(errorCode: ErrorCode): ResponseEntity<ErrorResponse> {
            return ResponseEntity.status(errorCode.httpStatus)
                .body(
                    ErrorResponse(
                        status = errorCode.httpStatus.value(),
                        error = errorCode.httpStatus.name,
                        code = errorCode.name,
                        message = errorCode.detail
                    )
                )
        }
    }
}
