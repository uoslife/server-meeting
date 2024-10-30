package uoslife.servermeeting.global.auth.dto.response

data class AccountResponse(
    val id: String,
    val nickname: String,
    val name: String?,
    val email: String?,
    val realm: Realm?,
    val identity: Identity?,
    val moderator: Moderator?,
    val isLinkedPortal: Boolean,
    val isVerified: Boolean,
    val verificationMethod: String?,
)

data class Realm(
    val name: String,
)

data class Identity(
    val id: String,
    val type: String,
    val status: String,
    val idNumber: String,
    val university: String?,
    val department: String?,
    val major: String?
)

data class Moderator(
    val generation: String,
    val chapter: String,
    val role: String,
)
