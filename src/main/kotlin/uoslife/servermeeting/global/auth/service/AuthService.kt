package uoslife.servermeeting.global.auth.service

import io.jsonwebtoken.Claims
import jakarta.servlet.http.HttpServletRequest
import org.apache.http.HttpEntity
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange
import uoslife.servermeeting.global.auth.dto.request.LoginRequest
import uoslife.servermeeting.global.auth.dto.response.TokenResponse
import uoslife.servermeeting.global.auth.dto.response.UserProfileVO
import uoslife.servermeeting.global.auth.exception.InvalidTokenException
import uoslife.servermeeting.global.auth.exception.LoginFailedException
import uoslife.servermeeting.global.auth.jwt.TokenProvider
import uoslife.servermeeting.global.auth.jwt.TokenType
import uoslife.servermeeting.user.entity.User
import uoslife.servermeeting.user.exception.UserAlreadyExistsException
import uoslife.servermeeting.user.exception.UserNotFoundException
import uoslife.servermeeting.user.repository.UserRepository
import uoslife.servermeeting.verification.dto.University
import uoslife.servermeeting.verification.service.VerificationService
import java.util.*


@Service
@Transactional(readOnly = true)
class AuthService(
    private val userRepository: UserRepository,
    private val tokenProvider: TokenProvider,
    private val verificationService: VerificationService,
) {
    companion object {
        private val logger = LoggerFactory.getLogger(AuthService::class.java)
    }
    @Transactional
    fun refreshAccessToken(request: HttpServletRequest): TokenResponse {
        val refreshToken: String =
            tokenProvider.resolveToken(request) ?: throw InvalidTokenException()
        val claims: Claims = tokenProvider.parseClaims(refreshToken, TokenType.REFRESH_SECRET)

        val user: User =
            userRepository.findByIdOrNull(UUID.fromString(claims.subject))
                ?: throw UserNotFoundException()
        val tokenResponse: TokenResponse = verificationService.getTokenByUser(user)

        return tokenResponse
    }

    // 시대생 앱 토큰으로 요청 시 회원가입 후 토큰 발급
    @Transactional
    fun migrateFromUoslife(bearerToken: String): TokenResponse {
        // rebuild-server에서 유저 데이터 가져오기
        val userProfileVO: UserProfileVO = getUserProfileFromUoslife(bearerToken)

        // 이미 회원가입 되어 있다면 예외 발생
        if(userRepository.existsByPhoneNumber(userProfileVO.phone)) throw UserAlreadyExistsException()

        // 회원가입 진행
        val savedUser: User = saveUser(userProfileVO)

        // 토큰 발급
        val tokenResponse: TokenResponse = verificationService.getTokenByUser(savedUser)
        return tokenResponse
    }

    private fun getUserProfileFromUoslife(bearerToken: String): UserProfileVO {
        val restTemplate: RestTemplate = RestTemplate()
        val url: String = "http://localhost:8081/core/users"

        // request header
        val headers: MultiValueMap<String, String> = LinkedMultiValueMap()
        headers.add("Content-type", "application/json")
        headers.add("accept", "application/json")
        headers.add("Authorization", bearerToken)

        // 리빌드 서버에서 유저 정보 가져오기
        val responseEntity: ResponseEntity<UserProfileVO> = restTemplate.exchange(url, HttpMethod.GET, org.springframework.http.HttpEntity<Any>(headers), UserProfileVO::class.java)
        val userProfileVO: UserProfileVO = responseEntity.body ?: throw RuntimeException()

        return userProfileVO
    }

    private fun saveUser(userProfileVO: UserProfileVO): User {
        val user: User = User(id = UUID.randomUUID(), phoneNumber = userProfileVO.phone, name = userProfileVO.name)
        user.userPersonalInformation.university = University.UOS

        val savedUser: User = userRepository.save(user)

        return savedUser
    }

    fun login(bearerToken: String): TokenResponse {
        // rebuild-server에서 유저 데이터 가져오기
        val userProfileVOFromUoslife: UserProfileVO = getUserProfileFromUoslife(bearerToken)

        // 비회원이라면 예외 발생
        val user: User = userRepository.findByPhoneNumber(userProfileVOFromUoslife.phone) ?: throw UserNotFoundException()

        // 토큰 발급
        val tokenResponse: TokenResponse = tokenProvider.getTokenByUser(user)

        return tokenResponse
    }
}
