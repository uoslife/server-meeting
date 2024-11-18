package uoslife.servermeeting.verification.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object VerificationUtils {

    fun generateRedisKey(prefix: String, email: String, isDate: Boolean = false): String {
        if (!isDate) {
            return "$prefix:$email"
        }
        return "$prefix:$email:${LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)}"
    }

    fun generateVerificationCode(): String {
        return String.format("%04d", (0..9999).random())
    }

    fun calculateExpirationTime(codeExpiry: Long): Long {
        return System.currentTimeMillis() + codeExpiry * 1000 // 현재 시간 + 유효 시간 (밀리초)
    }
}
