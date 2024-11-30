package uoslife.servermeeting.global.auth.security

import java.util.Base64
import java.util.concurrent.TimeUnit
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component

@Component
class JwtTokenStore(
    private val redisTemplate: RedisTemplate<String, Any>,
    @Value("\${jwt.refresh.expiration}") private val refreshTokenExpiration: Long,
    @Value("\${jwt.encryption.aes-secret-key}") private val aesSecretKey: String,
) {
    private val cipher: Cipher = Cipher.getInstance("AES")
    private val encryptionKey: SecretKey = SecretKeySpec(aesSecretKey.toByteArray(), "AES")

    fun saveRefreshToken(userId: Long, refreshToken: String) {
        val key = "${SecurityConstants.REFRESH_TOKEN_PREFIX}:$userId"
        val encryptedToken = encrypt(refreshToken)
        redisTemplate
            .opsForValue()
            .set(key, encryptedToken, refreshTokenExpiration, TimeUnit.MILLISECONDS)
    }

    fun getStoredRefreshToken(userId: Long): String? {
        val key = "${SecurityConstants.REFRESH_TOKEN_PREFIX}:$userId"
        val encryptedToken = redisTemplate.opsForValue().get(key)?.toString() ?: return null
        return decrypt(encryptedToken)
    }

    fun deleteRefreshToken(userId: Long) {
        val key = "${SecurityConstants.REFRESH_TOKEN_PREFIX}:$userId"
        redisTemplate.delete(key)
    }

    private fun encrypt(text: String): String {
        cipher.init(Cipher.ENCRYPT_MODE, encryptionKey)
        val encryptedBytes = cipher.doFinal(text.toByteArray())
        return Base64.getEncoder().encodeToString(encryptedBytes)
    }

    private fun decrypt(encryptedText: String): String {
        cipher.init(Cipher.DECRYPT_MODE, encryptionKey)
        val decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText))
        return String(decryptedBytes)
    }
}
