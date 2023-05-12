package uoslife.servermeeting.global.exception
import ErrorResponse
import jakarta.validation.ConstraintViolationException
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {
    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(value = [ConstraintViolationException::class, DataIntegrityViolationException::class])
    protected fun handleDataException(): ResponseEntity<ErrorResponse> {
        log.error("handleDataException throw Exception: {}", ErrorCode.DUPLICATE_RESOURCE)
        return ErrorResponse.toResponseEntity(ErrorCode.DUPLICATE_RESOURCE)
    }

    @ExceptionHandler(value = [CustomException::class])
    protected fun handleCustomException(e: CustomException): ResponseEntity<ErrorResponse> {
        log.error("handleCustomException throw CustomException: {}", e.errorCode)
        return ErrorResponse.toResponseEntity(e.errorCode)
    }
}
