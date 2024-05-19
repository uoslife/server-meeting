package uoslife.servermeeting.global.auth.service

import org.springframework.stereotype.Service
import uoslife.servermeeting.global.external.ServerClient
import uoslife.servermeeting.global.external.UOSLIFEUserDeviceResponse
import uoslife.servermeeting.global.external.UOSLIFEUserProfileResponse
import uoslife.servermeeting.global.external.UserClient

@Service
class UOSLIFEAccountService(
    private val userClient: UserClient,
    private val serverClient: ServerClient
) {
    fun getAuthenticatedUserProfile(token: String): UOSLIFEUserProfileResponse {
        return userClient.getAuthenticatedUserProfile(token)
    }

    fun getUserProfile(userId: Long): UOSLIFEUserProfileResponse {
        return serverClient.getUserProfile(userId)
    }

    fun getUserDevices(userId: Long): List<UOSLIFEUserDeviceResponse> {
        return serverClient.getUserDevices(userId)
    }

    fun getUsersPushTokens(userIds: List<Long>): List<String> {
        return serverClient.getUserPushTokens(userIds)
    }
}
