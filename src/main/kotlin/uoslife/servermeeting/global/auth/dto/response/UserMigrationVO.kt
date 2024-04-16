package uoslife.servermeeting.global.auth.dto.response

/**
 * 시대생 DB에 저장된 시대팅과 공유하는 유저 정보(name, phone)를 가져온다.
 */
data class UserMigrationVO(
    val name: String,
    val phone: String,
)
