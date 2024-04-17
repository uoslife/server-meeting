package uoslife.servermeeting.global.auth.service

import io.jsonwebtoken.Claims
import java.util.*
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.client.RestTemplate
import uoslife.servermeeting.global.auth.dto.response.TokenResponse
import uoslife.servermeeting.global.auth.dto.response.UserMigrationVO
import uoslife.servermeeting.global.auth.exception.ExternalApiFailedException
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
    companion object {
        const val UOSLIFE_URL = "https://api.uoslife.com/core/users"
        private val logger = LoggerFactory.getLogger(AuthService::class.java)
    }
    @Transactional
    fun refreshAccessToken(refreshToken: String): TokenResponse {
        tokenProvider.validateJwtToken(refreshToken, TokenType.REFRESH_SECRET)

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
        val userMigrationVOFromUoslife: UserMigrationVO = getUserProfileFromUoslife(bearerToken)

        // 회원가입 또는 이미 되어 있을 시 유저 반환
        val savedUser: User = createOrGetUser(userMigrationVOFromUoslife)

        // 토큰 발급
        val tokenResponse: TokenResponse = tokenProvider.getTokenByUser(savedUser)
        return tokenResponse
    }

    private fun getUserProfileFromUoslife(bearerToken: String): UserMigrationVO {
        val restTemplate: RestTemplate = RestTemplate()

        // request header
        val headers: MultiValueMap<String, String> = createHeader(bearerToken)

        // 리빌드 서버에서 유저 정보 가져오기
        try {
            val responseEntity: ResponseEntity<UserMigrationVO> =
                restTemplate.exchange(
                    UOSLIFE_URL,
                    HttpMethod.GET,
                    org.springframework.http.HttpEntity<Any>(headers),
                    UserMigrationVO::class.java
                )
            val userMigrationVO: UserMigrationVO = responseEntity.body!!

            return userMigrationVO
        } catch (e: HttpClientErrorException) {
            logger.info("HttpClientErrorExcpetion: UosLife로부터 통신 에러")
            throw ExternalApiFailedException()
        } catch (e: HttpServerErrorException) {
            logger.info("HttpServerErrorExcpetion: UosLife로부터 통신 에러")
            throw ExternalApiFailedException()
        } catch (e: Exception) {
            logger.info("Exception: UosLife로부터 통신 에러")
            throw ExternalApiFailedException()
        }
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
        val user: User =
            User(
                id = UUID.randomUUID(),
                phoneNumber = userMigrationVO.phone,
                name = userMigrationVO.name
            )
        user.userPersonalInformation.university = University.UOS
        val savedUser: User = userRepository.save(user)

        return user
    }
}
