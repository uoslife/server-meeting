package uoslife.servermeeting.global.auth.service

import feign.auth.BasicAuthRequestInterceptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import uoslife.servermeeting.global.auth.dto.response.AccountResponse

@Service
class AccountService {
    @Autowired lateinit var serverClient: ServerClient

    fun getUserProfile(userId: Long): AccountResponse {
        return serverClient.getUserProfile(userId)
    }
}

@FeignClient(
    name = "uoslife-account-api-server",
    url = "\${account.url}",
    configuration = [ServerClient.BasicAuthConfiguration::class]
)
interface ServerClient {
    @GetMapping("/v1/users/{userId}")
    fun getUserProfile(@PathVariable("userId") userId: Long): AccountResponse

    class BasicAuthConfiguration(
        @Value("\${account.access.id}") private val accessKeyId: String,
        @Value("\${account.access.secret}") private val accessKeySecret: String,
    ) {
        @Bean
        fun basicAuthRequestInterceptor(): BasicAuthRequestInterceptor {
            return BasicAuthRequestInterceptor(accessKeyId, accessKeySecret)
        }
    }
}
