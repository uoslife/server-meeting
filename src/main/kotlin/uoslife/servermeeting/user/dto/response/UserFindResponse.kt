package uoslife.servermeeting.user.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import uoslife.servermeeting.user.entity.enums.*
import uoslife.servermeeting.verification.dto.University

data class UserFindResponse(
    @Schema(description = "이름", example = "유현승") val name: String?,
    @Schema(description = "성별", example = "MALE") val genderType: GenderType?,
    @Schema(description = "전화번호", example = "01047324348") val phoneNumber: String?,
    @Schema(description = "나이", example = "26") val age: Int?,
    @Schema(description = "카카오톡 아이디", example = "__uhyun") val kakaoTalkId: String?,
    @Schema(description = "학과", example = "컴퓨터과학부") val department: String?,
    @Schema(description = "학생 타입(학부생, 대학원생, 졸업생", example = "UNDERGRADUATE")
    val studentType: StudentType?,
    @Schema(description = "키", example = "178") val height: Int?,
    @Schema(description = "종교", example = "CHRISTIAN") val religion: ReligionType?,
    @Schema(description = "최소 주량(병)", example = "1") val drinkingMin: Int?,
    @Schema(description = "최대 주량(병)", example = "3") val drinkingMax: Int?,
    @Schema(description = "흡연 여부", example = "FALSE") val smoking: SmokingType?,
    @Schema(description = "동물상", example = "[\"DOG\", \"CAT\"]")
    val spiritAnimal: List<SpiritAnimalType>?,
    @Schema(description = "MBTI", example = "INFP") val mbti: String?,
    @Schema(description = "흥미", example = "[\"EXERCISE\", \"MUSIC\"]")
    val interest: List<InterestType>?,
    @Schema(description = "대학", example = "UOS") val university: University?,
)
