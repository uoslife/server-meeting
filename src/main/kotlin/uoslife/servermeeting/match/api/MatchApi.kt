package uoslife.servermeeting.match.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import uoslife.servermeeting.global.error.ErrorResponse
import uoslife.servermeeting.match.dto.response.*
import uoslife.servermeeting.match.service.MatchingService
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

    @Operation(summary = "매칭 정보 조회", description = "매칭 결과와 매칭 상대의 정보를 조회합니다.")
    @ApiResponses(
        value =
            [
                ApiResponse(
                    responseCode = "200",
                    description = "매칭 결과 반환",
                    content = [Content(schema = Schema(implementation = MatchInfoResponse::class))]
                ),
                ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            name = "Meeting Team Not Found",
                                            description = "신청 내역 없음",
                                            value =
                                                "{\"message\": \"Meeting Team is not Found.\", \"status\": 400, \"code\": \"M06\"}"
                                        ),
                                        ExampleObject(
                                            name = "Payment Not Found",
                                            description = "결제 정보 없음",
                                            value =
                                                "{\"message\": \"Payment is not Found.\", \"status\": 400, \"code\": \"P01\"}"
                                        ),
                                    ]
                            )]
                ),
            ]
    )
    @GetMapping("/{teamType}/info")
    fun getMatchInformation(
        @AuthenticationPrincipal userDetails: UserDetails,
        @PathVariable teamType: TeamType,
        @RequestParam season: Int,
    ): ResponseEntity<MatchInfoResponse> {
        return ResponseEntity.ok(
            matchingService.getMatchInfo(userDetails.username.toLong(), teamType, season)
        )
    }
}
