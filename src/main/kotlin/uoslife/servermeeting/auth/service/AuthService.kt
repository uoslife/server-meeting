package uoslife.servermeeting.auth.service

import org.springframework.stereotype.Service
import uoslife.servermeeting.auth.dto.response.UserProfileResponse

@Service
class AuthService {

    fun authenticateToken(token: String):UserProfileResponse {}
}
