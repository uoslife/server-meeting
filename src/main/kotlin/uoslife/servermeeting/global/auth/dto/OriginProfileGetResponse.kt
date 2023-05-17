package uoslife.servermeeting.global.auth.dto

import java.util.*

data class OriginProfileGetResponse(
    val canuse_community: Boolean?,
    val id: UUID,
    val is_admin: Boolean?,
    val is_valid: Boolean?,
    val major_id: String?,
    val name: String?,
    val nickname: String?,
    val phone: String,
    val photo: String?,
    val processing: Boolean?,
    val sex: String?,
    val student_number: String?,
    val username: String,
    val verify_type: String,
)
