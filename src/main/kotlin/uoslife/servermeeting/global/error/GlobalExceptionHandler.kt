package uoslife.servermeeting.global.error

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import uoslife.servermeeting.global.error.exception.BusinessException
import uoslife.servermeeting.global.error.exception.EntityNotFoundException
import uoslife.servermeeting.global.error.exception.ErrorCode

@RestControllerAdvice
class GlobalExceptionHandler {
  private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

  @ExceptionHandler(BusinessException::class)
  protected fun handleBusinessException(
      exception: BusinessException,
  ): ResponseEntity<ErrorResponse> {
    logger.error("[Error] BusinessException", exception)
    val errorCode = exception.errorCode
    val response = ErrorResponse(errorCode)
    return ResponseEntity(response, HttpStatus.valueOf(errorCode.status))
  }

  @ExceptionHandler(EntityNotFoundException::class)
  protected fun handleEntityNotFoundException(
      exception: EntityNotFoundException,
  ): ResponseEntity<ErrorResponse> {
    logger.error("[Error] EntityNotFoundException", exception)
    val errorCode = exception.errorCode
    val response = ErrorResponse(errorCode)
    return ResponseEntity(response, HttpStatus.valueOf(errorCode.status))
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
  protected fun handleHttpRequestMethodNotSupportedException(
      exception: HttpRequestMethodNotSupportedException?,
  ): ResponseEntity<ErrorResponse> {
    logger.error("[Error] HttpRequestMethodNotSupportedException", exception)
    val errorCode = ErrorCode.METHOD_NOT_ALLOWED
    val response = ErrorResponse(errorCode)
    return ResponseEntity(response, HttpStatus.valueOf(errorCode.status))
  }

  @ExceptionHandler(MethodArgumentNotValidException::class)
  protected fun handleMethodArgumentNotValidException(
      exception: MethodArgumentNotValidException?,
  ): ResponseEntity<ErrorResponse> {
    logger.error("[Error] MethodArgumentTypeMismatchException", exception)
    val errorCode = ErrorCode.INVALID_INPUT_VALUE
    val response = ErrorResponse(errorCode)
    return ResponseEntity(response, HttpStatus.valueOf(errorCode.status))
  }

  @ExceptionHandler(HttpMessageNotReadableException::class)
  protected fun handleHttpMessageNotReadableException(
      exception: HttpMessageNotReadableException?,
  ): ResponseEntity<ErrorResponse> {
    logger.error("[Error] HttpMessageNotReadableException", exception)
    val errorCode = ErrorCode.INVALID_INPUT_VALUE
    val response = ErrorResponse(errorCode)
    return ResponseEntity(response, HttpStatus.valueOf(errorCode.status))
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException::class)
  fun handleHttpMessageNotReadableException(
      exception: MethodArgumentTypeMismatchException
  ): ResponseEntity<ErrorResponse> {
    logger.error("[Error] MethodArgumentTypeMismatchException", exception)
    val errorCode = ErrorCode.INVALID_INPUT_VALUE
    val response = ErrorResponse(errorCode)
    return ResponseEntity(response, HttpStatus.valueOf(errorCode.status))
  }
}
