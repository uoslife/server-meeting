package uoslife.servermeeting.global.auth.service

import java.util.*
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import uoslife.servermeeting.global.auth.dto.response.UserMigrationVO
import uoslife.servermeeting.user.entity.User
import uoslife.servermeeting.user.repository.UserRepository
import uoslife.servermeeting.verification.dto.University

@Service
@Transactional
class AuthService(
    private val userRepository: UserRepository,
) {
    companion object {
        private val logger = LoggerFactory.getLogger(AuthService::class.java)
    }
    private fun createHeader(bearerToken: String): MultiValueMap<String, String> {
        val headers: MultiValueMap<String, String> = LinkedMultiValueMap()
        headers.add("Content-type", "application/json")
        headers.add("accept", "application/json")
        headers.add("Authorization", bearerToken)

        return headers
    }

    private fun createOrGetUser(userMigrationVO: UserMigrationVO): User {
        // DB에 검색해서 있으면 가져오고 없으면 생성(회원가입)
        val user: User =
            userRepository.findByPhoneNumber(userMigrationVO.phone) ?: createUser(userMigrationVO)

        return user
    }

    private fun createUser(userMigrationVO: UserMigrationVO): User {
        val user: User = User(phoneNumber = userMigrationVO.phone, name = userMigrationVO.name)
        user.userPersonalInformation.university = University.UOS
        val savedUser: User = userRepository.save(user)

        return user
    }
}
