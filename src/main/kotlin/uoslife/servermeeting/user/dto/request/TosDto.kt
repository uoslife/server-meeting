package uoslife.servermeeting.user.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import uoslife.servermeeting.user.entity.Tos

@Schema(description = "약관 동의")
data class TosDto (
    var privatePolicy: Boolean? = false,
    var termsOfUse: Boolean? = false,
    var marketing: Boolean? = false,
    var doNotShare: Boolean? = false,
) {
    companion object {
        fun toEntity(tosDto: TosDto): Tos{
            val tos: Tos = Tos(
                privatePolicy = tosDto.privatePolicy ?: false,
                termsOfUse = tosDto.termsOfUse ?: false,
                marketing = tosDto.marketing ?: false,
                doNotShare = tosDto.doNotShare ?: false,
            )

            return tos
        }
    }
}
