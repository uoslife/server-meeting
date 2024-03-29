package uoslife.servermeeting.match.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import java.util.UUID
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import uoslife.servermeeting.global.error.ErrorResponse
import uoslife.servermeeting.match.dto.response.MatchInformationResponse
import uoslife.servermeeting.match.service.MatchingService

@RestController
@RequestMapping("/api/match")
@Tag(name = "Match", description = "매칭 API")
class MatchApi(
    private val matchingService: MatchingService,
) {
    @Operation(summary = "매칭된 미팅 팀 전체 정보 조회")
    @ApiResponses(
        value =
            [
                ApiResponse(
                    responseCode = "200",
                    description = "매칭된 미팅 팀 전체 정보(MatchInformationResponse) 반환",
                    content =
                        [Content(schema = Schema(implementation = MatchInformationResponse::class))]
                ),
                ApiResponse(
                    responseCode = "400",
                    description = "해당 유저 정보 없음",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            value =
                                                "{message: User is not Found., status: 400, code: U02}"
                                        )]
                            )]
                ),
                ApiResponse(
                    responseCode = "400",
                    description = "유저가 속한 팀이 매칭되어있지않음",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            value =
                                                "{message: Match is not Found., status: 400, code: MT01}"
                                        )]
                            )]
                ),
                ApiResponse(
                    responseCode = "400",
                    description = "유저가 일치하는 팀 정보 없음",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            value =
                                                "{message: Meeting Team is not Found., status: 400, code: M06}"
                                        )]
                            )]
                ),
                ApiResponse(
                    responseCode = "400",
                    description = "매칭된 상대의 선호 상대방에 대한 응답값이 없음",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            value =
                                                "{message: Preference is not Found., status: 400, code: M08}"
                                        )]
                            )]
                ),
                ApiResponse(
                    responseCode = "400",
                    description = "매칭된 상대의 질문 리스트 응답값이 없음",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            value =
                                                "{message: Information is not Found., status: 400, code: M07}"
                                        )]
                            )]
                ),
            ]
    )
    @GetMapping("")
    fun getMatchedMeetingTeamInformation(
        @AuthenticationPrincipal userDetails: UserDetails,
    ): ResponseEntity<MatchInformationResponse> {
        val userUUID = UUID.fromString(userDetails.username)
        return ResponseEntity.status(HttpStatus.OK)
            .body(matchingService.getMatchedMeetingTeam(userUUID))
    }
}
