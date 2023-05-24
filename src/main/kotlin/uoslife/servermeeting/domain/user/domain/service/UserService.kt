package uoslife.servermeeting.domain.user.domain.service

import org.springframework.stereotype.Service
import uoslife.servermeeting.domain.user.application.request.UserUpdateRequestDto
import uoslife.servermeeting.domain.user.domain.repository.UserRepository
import java.util.UUID

@Service
class UserService(userRepository: UserRepository) {
    fun updateUser(body: UserUpdateRequestDto, id: UUID): String {
        return "updateUser"
    }
}
