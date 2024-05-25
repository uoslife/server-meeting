package uoslife.servermeeting.global.external

data class UOSLIFEUserProfileResponse(
    val id: String,
    val nickname: String,
    val phone: String,
    val name: String?,
    val email: String?,
    val realm: Realm?,
    val identity: Identity?,
    val moderator: Moderator?,
    val isLinkedPortal: Boolean,
    val isVerified: Boolean,
    val verificationMethod: String?,
)

data class UOSLIFEUserDeviceResponse(
    val id: String,
    val model: String,
    val os: String,
    val osVersion: String,
    val appVersion: String,
    val codePushVersion: String,
    val firebasePushToken: String?,
    val lastUsedAt: String,
    val createdAt: String,
)

data class Realm(
    val code: String,
    val name: String,
)

data class Identity(
    val id: String,
    val type: String,
    val status: String,
    val idNumber: String,
    val university: String,
    val department: String,
    val major: String?,
)

data class Moderator(
    val generation: String,
    val chapter: String,
    val role: String,
)
