package uoslife.servermeeting.global.auth.cookie

import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Service
import uoslife.servermeeting.domain.user.domain.repository.UserRepository
import uoslife.servermeeting.global.auth.dto.OriginProfileGetResponse
import uoslife.servermeeting.global.auth.exception.ExternalApiFailedException
import uoslife.servermeeting.global.auth.exception.SessionCookieInvalidException
import uoslife.servermeeting.global.auth.exception.SessionCookieNotFoundException
import uoslife.servermeeting.global.util.CookieParser
import uoslife.servermeeting.global.util.RestTemplateRequester
import java.util.*

@Service
class AuthProviderService(
    private val cookieParser: CookieParser,
    private val restTemplateRequester: RestTemplateRequester,
    private val userRepository: UserRepository,
    @Value("\${app.originProfileGetUrl}")
    private val originProfileGetUrl: String,
) {

    fun getSessionCookieFromRequest(request: HttpServletRequest): String {
        return cookieParser.extractCookieValue(request, "session")
            ?: throw SessionCookieNotFoundException()
    }

    fun getAuthentication(sessionCookie: String): Authentication? {
        val originProfileGetResponse = getOriginProfile(sessionCookie)
        val authorities = getAuthorities()

        val userUUID = getUserUUID(originProfileGetResponse)
        val userDetails = User.builder()
            .username(userUUID.toString())
            .password("")
            .authorities(authorities)
            .build()
        return UsernamePasswordAuthenticationToken(userDetails, "", authorities)
    }

    fun getOriginProfile(sessionCookieValue: String): OriginProfileGetResponse {
        return try {
            restTemplateRequester.sendRequestWithCookie(
                "session=$sessionCookieValue",
                originProfileGetUrl,
                OriginProfileGetResponse::class.java,
            )
        } catch (externalApiFailedException: ExternalApiFailedException) {
            throw SessionCookieInvalidException()
        }
    }

    fun getAuthorities(): MutableList<GrantedAuthority> {
        val authorities: MutableList<GrantedAuthority> = ArrayList()
        authorities.add(SimpleGrantedAuthority("ROLE_USER"))

        return authorities
    }

    fun getUserUUID(originProfileGetResponse: OriginProfileGetResponse): UUID {
        // get User UUID
        val userUUID = originProfileGetResponse.id

        // check if user exists in database
        val userOptional = userRepository.findById(userUUID)
        if (userOptional.isEmpty) {
            userRepository.save(originProfileGetResponseToUser(originProfileGetResponse))
        }

        return userUUID
    }

    fun originProfileGetResponseToUser(originProfileGetResponse: OriginProfileGetResponse):
        uoslife.servermeeting.domain.user.domain.entity.User {
        return uoslife.servermeeting.domain.user.domain.entity.User(
            id = originProfileGetResponse.id,
            name = originProfileGetResponse.name,
            nickname = "user@" + UUID.randomUUID().toString(),
            phoneNumber = originProfileGetResponse.phone,
            profilePicture = originProfileGetResponse.photo,
        )
    }
}
