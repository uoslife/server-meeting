package uoslife.servermeeting.verification.util

import org.springframework.stereotype.Component

@Component
object VerificationConstants {
    const val CODE_PREFIX = "email_verification_code:"
    const val SEND_COUNT_PREFIX = "email_send_count"
    const val VERIFY_COUNT_PREFIX = "verification_attempts:"
    const val UOS_DOMAIN = "@uos.ac.kr"
}
