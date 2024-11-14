package uoslife.servermeeting.user.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import uoslife.servermeeting.user.dto.Interest
import uoslife.servermeeting.user.entity.enums.GenderType
import uoslife.servermeeting.user.entity.enums.InterestType
import uoslife.servermeeting.user.entity.enums.StudentType

data class CreateProfileRequest(
    @Schema(description = "이름", example = "유현승", nullable = false) @field:NotNull val name: String,
    @Schema(description = "성별", example = "MALE", nullable = false)
    @field:NotNull
    val gender: GenderType,
    @Schema(description = "나이", example = "26", nullable = false) @field:NotNull val age: Int,
    @Schema(description = "전화번호", example = "01047324348")
    @field:Size(max = 11)
    val phoneNumber: String?,
    @Schema(description = "카카오톡 아이디", example = "__uhyun", nullable = false)
    @field:NotNull
    val kakaoTalkId: String,
    @Schema(description = "학적 상태", example = "UNDERGRADUATE")
    val studentType: StudentType,
    @Schema(description = "학과", example = "컴퓨터과학부")
    val department: String,
    @Schema(description = "학번", example = "18") val studentNumber: Int?,
    @Schema(description = "관심사", example = "[{ \"name\": \"여행\", \"isDefault\": true }, { \"name\": \"맛집 탐방하기\", \"isDefault\": false }]")
    val interest: MutableList<InterestType>?,
) {}
