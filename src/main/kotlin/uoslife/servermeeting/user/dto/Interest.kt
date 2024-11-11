package uoslife.servermeeting.user.dto

import jakarta.persistence.Embeddable

@Embeddable
data class Interest(
    val name: String, // 관심사 이름
    var isDefault: Boolean = false // 기본 관심사 여부
)
