package uoslife.servermeeting.user.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import uoslife.servermeeting.user.entity.enums.*

data class UserUpdateRequest(
    @Schema(description = "이름", example = "유현승") @field:NotNull val name: String,
    @Schema(description = "성별", example = "MALE") @field:NotNull val gender: GenderType,
    @Schema(description = "전화번호", example = "01047324348")
    @field:NotNull
    @field:Size(max = 11)
    val phoneNumber: String,
    @Schema(description = "나이", example = "26") @field:NotNull val age: Int,
    @Schema(description = "카카오톡 아이디", example = "__uhyun") @field:NotNull val kakaoTalkId: String,
    @Schema(description = "학과", example = "컴퓨터과학부") @field:NotNull val department: String,
    @Schema(description = "학생 타입(학부생, 대학원생, 졸업생", example = "UNDERGRADUATE")
    @field:NotNull
    val studentType: StudentType,
    @Schema(description = "키", example = "178") val height: Int?,
    @Schema(description = "종교", example = "") val religion: ReligionType?,
    @Schema(description = "최소 주량(병)", example = "1") val drinkingMin: Int?,
    @Schema(description = "최대 주량(병)", example = "3") val drinkingMax: Int?,
    @Schema(description = "흡연 여부", example = "[FALSE]") val smoking: SmokingType?,
    @Schema(description = "동물상", example = "[DOG]") val spiritAnimal: List<SpiritAnimalType>?,
    @Schema(description = "MBTI", example = "INFP") val mbti: String?,
    @Schema(description = "흥미", example = "[EXERCISE]") val interest: List<InterestType>?,
    @Schema(description = "상대에게 전하는 첫 메시지", example = "안녕하세요 좋은 사람과 좋은 만남 가지고 싶네요 ㅎㅎㅎ")
    val message: String?,
)
