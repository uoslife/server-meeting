package uoslife.servermeeting.global.auth.service

import org.springframework.stereotype.Service
import uoslife.servermeeting.global.auth.dto.response.UserProfileResponse

@Service
class AuthService {

    fun authenticateToken(token: String): UserProfileResponse {}
}
