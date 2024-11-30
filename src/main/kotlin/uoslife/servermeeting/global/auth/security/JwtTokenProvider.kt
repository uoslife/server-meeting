package uoslife.servermeeting.global.auth.security

import org.springframework.stereotype.Component

@Component
class JwtTokenProvider(
    private val tokenGenerator: JwtTokenGenerator,
    private val tokenParser: JwtTokenParser,
    private val tokenStore: JwtTokenStore,
) {
    // Token 생성 관련 메서드
    fun createAccessToken(id: Long) = tokenGenerator.createAccessToken(id)
    fun createRefreshToken(id: Long) = tokenGenerator.createRefreshToken(id)

    // Token 검증 및 파싱 관련 메서드
    fun validateAccessToken(token: String) = tokenParser.validateAccessToken(token)
    fun validateRefreshToken(token: String) = tokenParser.validateRefreshToken(token)
    fun getUserIdFromAccessToken(token: String) = tokenParser.getUserIdFromAccessToken(token)
    fun getUserIdFromRefreshToken(token: String) = tokenParser.getUserIdFromRefreshToken(token)

    // Redis 저장소 관련 메서드
    fun saveRefreshToken(userId: Long, refreshToken: String) =
        tokenStore.saveRefreshToken(userId, refreshToken)
    fun getStoredRefreshToken(userId: Long) = tokenStore.getStoredRefreshToken(userId)
    fun deleteRefreshToken(userId: Long) = tokenStore.deleteRefreshToken(userId)
}
