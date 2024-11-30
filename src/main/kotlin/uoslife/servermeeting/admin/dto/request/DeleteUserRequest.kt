package uoslife.servermeeting.admin.dto.request

import io.swagger.v3.oas.annotations.media.Schema

data class DeleteUserRequest(@Schema(description = "유저 ID", example = "999") val userId: Long)
