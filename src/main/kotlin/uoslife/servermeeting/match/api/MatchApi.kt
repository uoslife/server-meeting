package uoslife.servermeeting.match.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import uoslife.servermeeting.global.error.ErrorResponse
import uoslife.servermeeting.match.dto.response.MatchResultResponse
import uoslife.servermeeting.match.dto.response.MeetingParticipationResponse
import uoslife.servermeeting.match.service.MatchingService
import uoslife.servermeeting.meetingteam.dto.response.MeetingTeamInformationGetResponse

@RestController
@RequestMapping("/api/match")
@Tag(name = "Match", description = "매칭 API")
class MatchApi(
    private val matchingService: MatchingService,
) {
    @Operation(summary = "시대팅 신청 내역 조회", description = "유저의 시대팅 신청 내역을 조회합니다.")
    @ApiResponses(
        value =
            [
                ApiResponse(
                    responseCode = "200",
                    description = "시대팅 신청 내역 반환",
                    content =
                        [
                            Content(
                                schema =
                                    Schema(implementation = MeetingParticipationResponse::class)
                            )]
                ),
            ]
    )
    @GetMapping("/participation")
    fun getMeetingParticipation(
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<MeetingParticipationResponse> {
        val result = matchingService.getMeetingParticipation(userDetails.username.toLong())
        return ResponseEntity.ok(result)
    }

    @Operation(summary = "매칭 결과 조회", description = "특정 매칭의 성공 여부를 조회합니다.")
    @ApiResponses(
        value =
            [
                ApiResponse(
                    responseCode = "200",
                    description = "매칭 결과 정보 반환",
                    content =
                        [Content(schema = Schema(implementation = MatchResultResponse::class))]
                ),
                ApiResponse(
                    responseCode = "403",
                    description = "해당 팀에 대한 접근 권한 없음",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            name = "MT03",
                                            value =
                                                "{\"message\": \"Unauthorized team access.\", \"status\": 403, \"code\": \"MT03\"}"
                                        )]
                            )]
                )]
    )
    @GetMapping("/{meetingTeamId}/result")
    fun getMatchResult(
        @PathVariable meetingTeamId: Long,
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<MatchResultResponse> {
        return ResponseEntity.ok(
            matchingService.getMatchResult(userDetails.username.toLong(), meetingTeamId)
        )
    }

    @Operation(summary = "매칭된 상대방 정보 조회", description = "매칭된 상대 팀의 상세 정보를 조회합니다.")
    @ApiResponses(
        value =
            [
                ApiResponse(
                    responseCode = "200",
                    description = "매칭된 상대방 정보 반환",
                    content =
                        [
                            Content(
                                schema =
                                    Schema(
                                        implementation = MeetingTeamInformationGetResponse::class
                                    )
                            )]
                ),
                ApiResponse(
                    responseCode = "400",
                    description = "매치를 찾을 수 없음",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            name = "MT01",
                                            value =
                                                "{\"message\": \"Match is not Found.\", \"status\": 400, \"code\": \"MT01\"}"
                                        )]
                            )]
                ),
                ApiResponse(
                    responseCode = "403",
                    description = "해당 매치에 대한 접근 권한 없음",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            name = "MT04",
                                            value =
                                                "{\"message\": \"Unauthorized match access.\", \"status\": 403, \"code\": \"MT04\"}"
                                        )]
                            )]
                ),
            ]
    )
    @GetMapping("/{matchId}/partner")
    fun getMatchedMeetingTeamInformation(
        @PathVariable matchId: Long,
        @AuthenticationPrincipal userDetails: UserDetails,
    ): ResponseEntity<MeetingTeamInformationGetResponse> {
        return ResponseEntity.status(HttpStatus.OK)
            .body(
                matchingService.getMatchPartnerInformation(userDetails.username.toLong(), matchId)
            )
    }
}
