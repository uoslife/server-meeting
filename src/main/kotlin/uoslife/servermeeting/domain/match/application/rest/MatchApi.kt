package uoslife.servermeeting.domain.match.application.rest

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import uoslife.servermeeting.domain.match.domain.service.impl.MatchingService
import uoslife.servermeeting.domain.meeting.application.response.MeetingTeamInformationGetResponse
import java.util.*

@RestController
@RequestMapping("/api/match")
@Tag(name = "Match", description = "매칭 API")
class MatchApi(
    private val matchingService: MatchingService,
) {
    @Operation(summary = "매칭된 미팅 팀 전체 정보 조회")
    @ApiResponse(
        responseCode = "200",
        description = "매칭된 미팅 팀 전체 정보(MeetingTeamInformationGetResponse) 반환",
    )
    @GetMapping("")
    fun getMatchedMeetingTeamInformation(
        @AuthenticationPrincipal userDetails: UserDetails,
    ): ResponseEntity<MeetingTeamInformationGetResponse> {
        val userUUID = UUID.fromString(userDetails.username)
        return ResponseEntity.status(HttpStatus.OK).body(matchingService.getMatchedMeetingTeam(userUUID))
    }
}
