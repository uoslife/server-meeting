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
import org.springframework.web.bind.annotation.*
import uoslife.servermeeting.global.error.ErrorResponse
import uoslife.servermeeting.match.dto.response.MatchResultResponse
import uoslife.servermeeting.match.dto.response.MatchedPartnerInformationResponse
import uoslife.servermeeting.match.dto.response.MeetingParticipationResponse
import uoslife.servermeeting.match.service.MatchingService
import uoslife.servermeeting.meetingteam.dto.response.MeetingTeamInformationGetResponse
import uoslife.servermeeting.meetingteam.entity.enums.TeamType

@RestController
@RequestMapping("/api/match")
@Tag(name = "Match", description = "매칭 내역 조회 API")
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
    @GetMapping("/me/participations")
    fun getUserMeetingParticipation(
        @AuthenticationPrincipal userDetails: UserDetails,
        @RequestParam season: Int,
    ): ResponseEntity<MeetingParticipationResponse> {
        val result =
            matchingService.getUserMeetingParticipation(userDetails.username.toLong(), season)
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
                    responseCode = "400",
                    description = "해당 타입의 미팅 신청 내역 없음",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            name = "Meeting Team Not Found",
                                            value =
                                                "{\"message\": \"Meeting Team is not Found.\", \"status\": 400, \"code\": \"M06\"}"
                                        )]
                            )]
                )]
    )
    @GetMapping("/{teamType}/result")
    fun getMatchResult(
        @AuthenticationPrincipal userDetails: UserDetails,
        @PathVariable teamType: TeamType,
        @RequestParam season: Int,
    ): ResponseEntity<MatchResultResponse> {
        return ResponseEntity.ok(
            matchingService.getMatchResult(userDetails.username.toLong(), teamType, season)
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
                    description = "신청 내역 또는 성공 내역 없음",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            name = "Meeting Team Not Found",
                                            value =
                                                "{\"message\": \"Meeting Team is not Found.\", \"status\": 400, \"code\": \"M06\"}"
                                        ),
                                        ExampleObject(
                                            name = "Match Not Found",
                                            value =
                                                "{\"message\": \"Match is not Found.\", \"status\": 400, \"code\": \"MT01\"}"
                                        )]
                            )]
                ),
            ]
    )
    @GetMapping("/{teamType}/partner")
    fun getMatchedPartnerInformation(
        @AuthenticationPrincipal userDetails: UserDetails,
        @PathVariable teamType: TeamType,
        @RequestParam season: Int,
    ): ResponseEntity<MatchedPartnerInformationResponse> {
        return ResponseEntity.status(HttpStatus.OK)
            .body(
                matchingService.getMatchedPartnerInformation(
                    userDetails.username.toLong(),
                    teamType,
                    season
                )
            )
    }
}
