package uoslife.servermeeting.global.external

import feign.auth.BasicAuthRequestInterceptor
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.context.annotation.Bean
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(name = "uoslife-account-api-user", url = "\${account.url}")
interface UserClient {
    @GetMapping("/v1/users/me")
    fun getAuthenticatedUserProfile(
        @RequestHeader("Authorization") token: String,
    ): UOSLIFEUserProfileResponse?
}

@FeignClient(
    name = "uoslife-account-api-server",
    url = "\${account.url}",
    configuration = [ServerClient.BasicAuthConfiguration::class]
)
interface ServerClient {
    @GetMapping("/v1/users/{userId}")
    fun getUserProfile(@PathVariable("userId") userId: Long): UOSLIFEUserProfileResponse

    @GetMapping("/v1/users/{userId}/devices")
    fun getUserDevices(@PathVariable("userId") userId: Long): List<UOSLIFEUserDeviceResponse>

    @GetMapping("/v1/push-tokens")
    fun getUserPushTokens(@RequestParam("userIds") userId: List<Long>): List<String>

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
