package uoslife.servermeeting.meetingteam.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import uoslife.servermeeting.global.error.ErrorResponse
import uoslife.servermeeting.meetingteam.dto.request.MeetingTeamInfoUpdateRequest
import uoslife.servermeeting.meetingteam.dto.response.MeetingTeamCodeResponse
import uoslife.servermeeting.meetingteam.dto.response.MeetingTeamInformationGetResponse
import uoslife.servermeeting.meetingteam.dto.response.MeetingTeamUserListGetResponse
import uoslife.servermeeting.meetingteam.entity.enums.TeamType
import uoslife.servermeeting.meetingteam.exception.InSingleMeetingTeamNoJoinTeamException
import uoslife.servermeeting.meetingteam.exception.InSingleMeetingTeamOnlyOneUserException
import uoslife.servermeeting.meetingteam.service.BaseMeetingService

@RestController
@RequestMapping("/api/meeting")
@Tag(name = "Meeting", description = "미팅 API")
class MeetingApi(
    @Qualifier("singleMeetingService") private val singleMeetingService: BaseMeetingService,
    @Qualifier("tripleMeetingService") private val tripleMeetingService: BaseMeetingService,
) {

    @Operation(summary = "미팅 팀 생성", description = "리더만 팀 생성 가능")
    @ApiResponses(
        value =
            [
                ApiResponse(
                    responseCode = "201",
                    description =
                        "1대1의 경우 빈 문자열을 반환, 3대3의 경우 팀 코드(A-Z,0-9 4개)(String)를 반환 (팀 이름은 2~8글자)",
                    content =
                        [Content(schema = Schema(implementation = MeetingTeamCodeResponse::class))]
                ),
                ApiResponse(
                    responseCode = "400",
                    description = "요청 값에 문제가 있음.",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            name = "U02",
                                            description = "해당 유저 정보 없음",
                                            value =
                                                "{message: User is not Found., status: 400, code: U02}"
                                        ),
                                        ExampleObject(
                                            name = "M02",
                                            description = "유저가 이미 팀에 속해있음",
                                            value =
                                                "{message: User already have Team., status: 400, code: M02}"
                                        ),
                                        ExampleObject(
                                            name = "M11",
                                            description = "팀 이름이 적절하지 못함",
                                            value =
                                                "{message: Team name is invalid., status: 400, code: M11}"
                                        ),
                                        ExampleObject(
                                            name = "M12",
                                            description = "팀 코드 생성에 실패함",
                                            value =
                                                "{message: Team Code Generate is Failed., status: 500, code: M12}"
                                        )]
                            )]
                ),
                ApiResponse(
                    responseCode = "401",
                    description = "부적절한 토큰 정보",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            value =
                                                "{message: Token is not valid., status: 401, code: T01}"
                                        )]
                            )]
                ),
            ]
    )
    @PostMapping("/{teamType}/create")
    fun createMeetingTeam(
        @AuthenticationPrincipal userDetails: UserDetails,
        @PathVariable teamType: TeamType,
        @RequestParam(required = false) name: String?,
    ): ResponseEntity<MeetingTeamCodeResponse> {
        val userId = userDetails.username.toLong()

        val meetingTeamCodeResponse =
            when (teamType) {
                TeamType.SINGLE -> singleMeetingService.createMeetingTeam(userId, name)
                TeamType.TRIPLE -> tripleMeetingService.createMeetingTeam(userId, name)
            }

        return ResponseEntity.ok(meetingTeamCodeResponse)
    }

    @Operation(summary = "미팅 팀 참가", description = "1대1의 경우 지원되지 않음. 1대1은 미팅 팀 생성 시 자동으로 참가됨")
    @ApiResponses(
        value =
            [
                ApiResponse(
                    responseCode = "204",
                    description = "팀에 참가하고 참가한 팀 정보 반환",
                    content =
                        [
                            Content(
                                schema =
                                    Schema(implementation = MeetingTeamUserListGetResponse::class)
                            )]
                ),
                ApiResponse(
                    responseCode = "400",
                    description = "요청 값에 문제가 있음",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            name = "U02",
                                            description = "해당 유저 정보 없음",
                                            value =
                                                "{message: User is not Found., status: 400, code: U02}"
                                        ),
                                        ExampleObject(
                                            name = "M06",
                                            description = "유저가 일치하는 팀 정보 없음",
                                            value =
                                                "{message: Meeting Team is not Found., status: 400, code: M06}"
                                        ),
                                        ExampleObject(
                                            name = "M16",
                                            description = "해당 팀에 팀장이 없음",
                                            value =
                                                "{message: Team Leader is not Found., status: 400, code: M16}"
                                        ),
                                        ExampleObject(
                                            name = "M13",
                                            description = "팀 코드가 일치하지 않음",
                                            value =
                                                "{message: Team Code is Invalid Format., status: 400, code: M13}"
                                        ),
                                        ExampleObject(
                                            name = "M02",
                                            description = "유저가 이미 팀을 가지고 있음",
                                            value =
                                                "{message: User already have Team., status: 400, code: M02}"
                                        ),
                                        ExampleObject(
                                            name = "M14",
                                            description = "해당 팀의 정원이 꽉참",
                                            value =
                                                "{message: Team is Full., status: 400, code: M14}"
                                        ),
                                        ExampleObject(
                                            name = "M17",
                                            description = "다른 성별의 팀에 입장 불가",
                                            value =
                                                "{message: Team must consist of Same Gender, status: 400, code: M17}"
                                        )]
                            )]
                ),
                ApiResponse(
                    responseCode = "401",
                    description = "부적절한 토큰 정보",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            value =
                                                "{message: Token is not valid., status: 401, code: T01}"
                                        )]
                            )]
                ),
            ]
    )
    @PostMapping("/{teamType}/join/{code}")
    fun joinMeetingTeam(
        @AuthenticationPrincipal userDetails: UserDetails,
        @PathVariable teamType: TeamType,
        @PathVariable code: String,
    ): ResponseEntity<MeetingTeamUserListGetResponse?> {
        val userId = userDetails.username.toLong()

        if (teamType == TeamType.SINGLE) {
            throw InSingleMeetingTeamNoJoinTeamException()
        }

        val meetingTeamUserListGetResponse = tripleMeetingService.joinMeetingTeam(userId, code)
        return ResponseEntity.ok(meetingTeamUserListGetResponse)
    }

    @Operation(
        summary = "팅 결성 대기 중 간에 미팅 팀 유저 리스트 조회",
        description = "1대1의 경우 지원되지 않음. 1대1은 팀 유저가 본인 단독"
    )
    @ApiResponses(
        value =
            [
                ApiResponse(
                    responseCode = "200",
                    description = "미팅 팀 유저 리스트 및 팀 이름(MeetingTeamUserListGetResponse) 반환",
                    content =
                        [
                            Content(
                                schema =
                                    Schema(implementation = MeetingTeamUserListGetResponse::class)
                            )]
                ),
                ApiResponse(
                    responseCode = "400",
                    description = "요청 값에 문제가 있음",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            name = "U02",
                                            description = "해당 유저 정보 없음",
                                            value =
                                                "{message: User is not Found., status: 400, code: U02}"
                                        ),
                                        ExampleObject(
                                            name = "M06",
                                            description = "유저가 일치하는 팀 정보 없음",
                                            value =
                                                "{message: Meeting Team is not Found., status: 400, code: M06}"
                                        ),
                                        ExampleObject(
                                            name = "M05",
                                            description = "1대1 팀의 경우는 팀에 속한 유저 리스트 조회 불가",
                                            value =
                                                "{message: In Single Meeting Team, only One User Exist., status: 400, code: M05}"
                                        )]
                            )]
                ),
                ApiResponse(
                    responseCode = "401",
                    description = "부적절한 토큰 정보",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            value =
                                                "{message: Token is not valid., status: 401, code: T01}"
                                        )]
                            )]
                ),
            ]
    )
    @GetMapping("/{teamType}/{code}/user/list")
    fun getMeetingTeamUserList(
        @AuthenticationPrincipal userDetails: UserDetails,
        @PathVariable code: String,
        @PathVariable teamType: TeamType,
    ): ResponseEntity<MeetingTeamUserListGetResponse> {
        val userId = userDetails.username.toLong()

        if (teamType == TeamType.SINGLE) {
            throw InSingleMeetingTeamOnlyOneUserException()
        }

        val meetingTeamUserListGetResponse =
            tripleMeetingService.getMeetingTeamUserList(userId, code)

        return ResponseEntity.status(HttpStatus.OK).body(meetingTeamUserListGetResponse)
    }

    @Operation(
        summary = "미팅 팀 정보 기입",
        description = "팀이 원하는 상대, 팀 메시지 정보를 기입함. 리더만 가능. 모든 정보를 채워서 보냄"
    )
    @ApiResponses(
        value =
            [
                ApiResponse(
                    responseCode = "204",
                    description = "반환값 없음",
                    content = [Content(schema = Schema(implementation = Unit::class))]
                ),
                ApiResponse(
                    responseCode = "400",
                    description = "요청 값에 문제가 있음",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            name = "U02",
                                            description = "해당 유저 정보 없음",
                                            value =
                                                "{message: User is not Found., status: 400, code: U02}"
                                        ),
                                        ExampleObject(
                                            name = "M06",
                                            description = "유저가 일치하는 팀 정보 없음",
                                            value =
                                                "{message: Meeting Team is not Found., status: 400, code: M06}"
                                        )]
                            )]
                ),
                ApiResponse(
                    responseCode = "401",
                    description = "부적절한 토큰 정보",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            value =
                                                "{message: Token is not valid., status: 401, code: T01}"
                                        )]
                            )]
                ),
            ]
    )
    @PutMapping("/{teamType}/info")
    fun updateMeetingTeamInfo(
        @AuthenticationPrincipal userDetails: UserDetails,
        @PathVariable teamType: TeamType,
        @RequestBody @Valid meetingTeamInfoUpdateRequest: MeetingTeamInfoUpdateRequest,
    ): ResponseEntity<Unit> {
        val userId = userDetails.username.toLong()

        when (teamType) {
            TeamType.SINGLE ->
                singleMeetingService.updateMeetingTeamInfo(
                    userId,
                    meetingTeamInfoUpdateRequest,
                )
            TeamType.TRIPLE ->
                tripleMeetingService.updateMeetingTeamInfo(
                    userId,
                    meetingTeamInfoUpdateRequest,
                )
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }

    @Operation(summary = "미팅 팀 전체 정보 조회")
    @ApiResponses(
        value =
            [
                ApiResponse(
                    responseCode = "200",
                    description = "미팅 팀 전체 정보(MeetingTeamInformationGetResponse) 반환",
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
                    description = "요청 값에 문제가 있음",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            name = "U02",
                                            description = "해당 유저 정보 없음",
                                            value =
                                                "{message: User is not Found., status: 400, code: U02}"
                                        ),
                                        ExampleObject(
                                            name = "M06",
                                            description = "유저가 일치하는 팀 정보 없음",
                                            value =
                                                "{message: Meeting Team is not Found., status: 400, code: M06}"
                                        ),
                                        ExampleObject(
                                            name = "M08",
                                            description = "매칭된 상대의 선호 상대방에 대한 응답값이 없음",
                                            value =
                                                "{message: Preference is not Found., status: 400, code: M08}"
                                        ),
                                        ExampleObject(
                                            name = "M07",
                                            description = "매칭된 상대의 질문 리스트 응답값이 없음",
                                            value =
                                                "{message: Information is not Found., status: 400, code: M07}"
                                        )]
                            )]
                ),
                ApiResponse(
                    responseCode = "401",
                    description = "부적절한 토큰 정보",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            value =
                                                "{message: Token is not valid., status: 401, code: T01}"
                                        )]
                            )]
                ),
            ]
    )
    @GetMapping("/{teamType}/application/info")
    fun getMeetingTeamApplicationInformation(
        @AuthenticationPrincipal userDetails: UserDetails,
        @PathVariable teamType: TeamType,
    ): ResponseEntity<MeetingTeamInformationGetResponse> {
        val userId = userDetails.username.toLong()

        val meetingTeamInformationGetResponse =
            when (teamType) {
                TeamType.SINGLE -> singleMeetingService.getMeetingTeamInformation(userId)
                TeamType.TRIPLE -> tripleMeetingService.getMeetingTeamInformation(userId)
            }
        return ResponseEntity.status(HttpStatus.OK).body(meetingTeamInformationGetResponse)
    }

    @Operation(summary = "미팅 팀 삭제", description = "리더만 팀 삭제 가능")
    @ApiResponses(
        value =
            [
                ApiResponse(
                    responseCode = "204",
                    description = "반환값 없음",
                    content = [Content(schema = Schema(implementation = Unit::class))]
                ),
                ApiResponse(
                    responseCode = "400",
                    description = "요청 값에 문제가 있음",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            name = "U02",
                                            description = "해당 유저 정보 없음",
                                            value =
                                                "{message: User is not Found., status: 400, code: U02}"
                                        ),
                                        ExampleObject(
                                            name = "M06",
                                            description = "유저가 일치하는 팀 정보 없음",
                                            value =
                                                "{message: Meeting Team is not Found., status: 400, code: M06}"
                                        )]
                            )]
                ),
                ApiResponse(
                    responseCode = "401",
                    description = "부적절한 토큰 정보",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            value =
                                                "{message: Token is not valid., status: 401, code: T01}"
                                        )]
                            )]
                ),
            ]
    )
    @DeleteMapping("/{teamType}")
    fun deleteMeetingTeam(
        @AuthenticationPrincipal userDetails: UserDetails,
        @PathVariable teamType: TeamType,
    ): ResponseEntity<Unit> {
        val userId = userDetails.username.toLong()

        when (teamType) {
            TeamType.SINGLE -> singleMeetingService.deleteMeetingTeam(userId)
            TeamType.TRIPLE -> tripleMeetingService.deleteMeetingTeam(userId)
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }
}
