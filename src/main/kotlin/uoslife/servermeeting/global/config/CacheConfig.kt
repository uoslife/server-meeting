package uoslife.servermeeting.global.config

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.time.Duration
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
@EnableCaching
class CacheConfig(private val redisConnectionFactory: RedisConnectionFactory) {
    @Bean
    fun cacheManager(): CacheManager {
        val objectMapper =
            ObjectMapper().registerModule(KotlinModule.Builder().build()).apply {
                configure(MapperFeature.USE_STD_BEAN_NAMING, true)
                configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                activateDefaultTyping(
                    BasicPolymorphicTypeValidator.builder()
                        .allowIfBaseType(Any::class.java)
                        .build(),
                    ObjectMapper.DefaultTyping.EVERYTHING
                )
            }
        val defaultConfig =
            RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(24))
                .serializeKeysWith(
                    RedisSerializationContext.SerializationPair.fromSerializer(
                        StringRedisSerializer()
                    )
                )
                .serializeValuesWith(
                    RedisSerializationContext.SerializationPair.fromSerializer(
                        GenericJackson2JsonRedisSerializer(objectMapper)
                    )
                )

        val configurations =
            mapOf(
                "meeting-participation" to defaultConfig.entryTtl(Duration.ofDays(2)),
                "match-info" to defaultConfig.entryTtl(Duration.ofDays(2))
            )

        return RedisCacheManager.builder(redisConnectionFactory)
            .cacheDefaults(defaultConfig)
            .withInitialCacheConfigurations(configurations)
            .build()
    }
}
