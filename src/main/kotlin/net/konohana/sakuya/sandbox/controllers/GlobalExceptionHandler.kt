package net.konohana.sakuya.sandbox.controllers

import net.konohana.sakuya.sandbox.models.ApiErrorResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * アプリケーション共通の例外ハンドラー。
 *
 * 各種例外を捕捉し、統一された [ApiErrorResponse] 形式で HTTP レスポンスを返却します。
 */
@RestControllerAdvice
class GlobalExceptionHandler {

    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    /**
     * 不正な引数例外 ([IllegalArgumentException]) を捕捉し、400 Bad Request を返却します。
     *
     * @param ex 捕捉した例外
     * @return 400 エラーレスポンス
     */
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): ResponseEntity<ApiErrorResponse> {
        logger.warn("Bad request parameter encountered: {}", ex.message)
        val errorResponse = ApiErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            error = HttpStatus.BAD_REQUEST.reasonPhrase,
            message = ex.message ?: "Invalid request parameter",
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    /**
     * 予期せぬシステム例外 ([Exception]) を捕捉し、500 Internal Server Error を返却します。
     *
     * @param ex 捕捉した例外
     * @return 500 エラーレスポンス
     */
    @ExceptionHandler(Exception::class)
    fun handleGeneralException(ex: Exception): ResponseEntity<ApiErrorResponse> {
        logger.error("Unhandled server exception occurred", ex)
        val errorResponse = ApiErrorResponse(
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            error = HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase,
            message = "An unexpected error occurred. Please contact the administrator.",
        )
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse)
    }
}
