package uoslife.servermeeting.user.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import uoslife.servermeeting.user.command.TeamBranch

data class UserBranchResponse(
    @Schema(description = "1:1 상태", example = "COMPLETED") val singleTeamBranch: TeamBranch,
    @Schema(description = "3:3 상태", example = "NOT_CREATED") val tripleTeamBranch: TeamBranch
)
