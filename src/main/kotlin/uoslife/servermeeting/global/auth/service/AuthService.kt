package uoslife.servermeeting.global.auth.service

import io.jsonwebtoken.Claims
import jakarta.servlet.http.HttpServletRequest
import java.util.*
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import uoslife.servermeeting.global.auth.dto.response.TokenResponse
import uoslife.servermeeting.global.auth.dto.response.UserProfileVO
import uoslife.servermeeting.global.auth.exception.InvalidTokenException
import uoslife.servermeeting.global.auth.jwt.TokenProvider
import uoslife.servermeeting.global.auth.jwt.TokenType
import uoslife.servermeeting.user.entity.User
import uoslife.servermeeting.user.exception.UserNotFoundException
import uoslife.servermeeting.user.repository.UserRepository
import uoslife.servermeeting.verification.dto.University

@Service
@Transactional
class AuthService(
    private val userRepository: UserRepository,
    private val tokenProvider: TokenProvider,
) {
    @Transactional
    fun refreshAccessToken(request: HttpServletRequest): TokenResponse {
        val refreshToken: String =
            tokenProvider.resolveToken(request) ?: throw InvalidTokenException()
        val claims: Claims = tokenProvider.parseClaims(refreshToken, TokenType.REFRESH_SECRET)

        val user: User =
            userRepository.findByIdOrNull(UUID.fromString(claims.subject))
                ?: throw UserNotFoundException()
        val tokenResponse: TokenResponse = tokenProvider.getTokenByUser(user)

        return tokenResponse
    }

    // 시대생 앱 토큰으로 요청 시 회원가입(or 로그인) 후 토큰 발급
    @Transactional
    fun signUpOrInFromUoslife(bearerToken: String): TokenResponse {
        // rebuild-server에서 유저 데이터 가져오기
        val userProfileVOFromUoslife: UserProfileVO = getUserProfileFromUoslife(bearerToken)

        // 회원가입 또는 이미 되어 있을 시 유저 반환
        val savedUser: User = createOrGetUser(userProfileVOFromUoslife)

        // 토큰 발급
        val tokenResponse: TokenResponse = tokenProvider.getTokenByUser(savedUser)
        return tokenResponse
    }

    private fun getUserProfileFromUoslife(bearerToken: String): UserProfileVO {
        val restTemplate: RestTemplate = RestTemplate()
        val url: String = "https://api.uoslife.com/core/users"

        // request header
        val headers: MultiValueMap<String, String> = LinkedMultiValueMap()
        headers.add("Content-type", "application/json")
        headers.add("accept", "application/json")
        headers.add("Authorization", bearerToken)

        // 리빌드 서버에서 유저 정보 가져오기
        val responseEntity: ResponseEntity<UserProfileVO> =
            restTemplate.exchange(
                url,
                HttpMethod.GET,
                org.springframework.http.HttpEntity<Any>(headers),
                UserProfileVO::class.java
            )
        val userProfileVO: UserProfileVO = responseEntity.body ?: throw RuntimeException()

        return userProfileVO
    }

    private fun createOrGetUser(userProfileVO: UserProfileVO): User {
        // DB에 검색해서 있으면 가져오고 없으면 생성(회원가입)
        val user: User =
            userRepository.findByPhoneNumber(userProfileVO.phone) ?: createUser(userProfileVO)

        return user
    }

    private fun createUser(userProfileVO: UserProfileVO): User {
        val user: User =
            User(
                id = UUID.randomUUID(),
                phoneNumber = userProfileVO.phone,
                name = userProfileVO.name
            )
        user.userPersonalInformation.university = University.UOS
        val savedUser: User = userRepository.save(user)

        return user
    }
}
