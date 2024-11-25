package uoslife.servermeeting.global.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfig {

    @Value("\${spring.data.redis.host}") private lateinit var host: String

    @Value("\${spring.data.redis.port}") private var port: Int = 0

    @Value("\${spring.data.redis.password:}") // 비밀번호가 없는 경우 기본값은 빈 문자열
    private lateinit var password: String

    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory {
        val config =
            RedisStandaloneConfiguration().apply {
                this.hostName = host
                this.port = port
                if (password.isPresent) { // 비밀번호가 설정된 경우에만 추가
                    this.password = password
                }
            }
        return LettuceConnectionFactory(config)
    }
    @Bean
    fun redisTemplate(): RedisTemplate<String, Any> {
        return RedisTemplate<String, Any>().apply {
            setConnectionFactory(redisConnectionFactory())

            keySerializer = StringRedisSerializer()
            valueSerializer = StringRedisSerializer()
            hashKeySerializer = StringRedisSerializer()
            hashValueSerializer = StringRedisSerializer()
        }
    }
}
