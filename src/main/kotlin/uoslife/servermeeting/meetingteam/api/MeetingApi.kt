package uoslife.servermeeting.meetingteam.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import java.util.UUID
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
import uoslife.servermeeting.meetingteam.dto.request.MeetingTeamInformationUpdateRequest
import uoslife.servermeeting.meetingteam.dto.request.MeetingTeamMessageUpdateRequest
import uoslife.servermeeting.meetingteam.dto.request.MeetingTeamPreferenceUpdateRequest
import uoslife.servermeeting.meetingteam.dto.response.MeetingTeamInformationGetResponse
import uoslife.servermeeting.meetingteam.dto.response.MeetingTeamUserListGetResponse
import uoslife.servermeeting.meetingteam.entity.enums.TeamType
import uoslife.servermeeting.meetingteam.exception.InSingleMeetingTeamNoJoinTeamException
import uoslife.servermeeting.meetingteam.exception.InSingleMeetingTeamOnlyOneUserException
import uoslife.servermeeting.meetingteam.exception.OnlyTeamLeaderCanCreateTeamException
import uoslife.servermeeting.meetingteam.exception.OnlyTeamLeaderCanDeleteTeamException
import uoslife.servermeeting.meetingteam.exception.OnlyTeamLeaderCanUpdateTeamInformationException
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
                    description = "1대1의 경우 빈 문자열을 반환, 3대3의 경우 팀 코드(A-Z0-9 4개)(String)를 반환",
                    content = [Content(schema = Schema(implementation = String::class))]
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
                    description = "유저가 이미 팀에 속해있음",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            value =
                                                "{message: User already have Team., status: 400, code: M02}"
                                        )]
                            )]
                ),
                ApiResponse(
                    responseCode = "400",
                    description = "유저가 이미 팀에 속해있음",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            value =
                                                "{message: Team name is invalid., status: 400, code: M11}"
                                        )]
                            )]
                ),
                ApiResponse(
                    responseCode = "400",
                    description = "유저가 이미 팀에 속해있음",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            value =
                                                "{message: Team Code Generate is Failed., status: 500, code: M12}"
                                        )]
                            )]
                ),
            ]
    )
    @PostMapping("/{teamType}/{isTeamLeader}/create")
    fun createMeetingTeam(
        @AuthenticationPrincipal userDetails: UserDetails,
        @PathVariable teamType: TeamType,
        @PathVariable isTeamLeader: Boolean,
        @RequestParam(required = false) name: String?,
    ): ResponseEntity<String?> {
        val userUUID = UUID.fromString(userDetails.username)

        if (!isTeamLeader) {
            throw OnlyTeamLeaderCanCreateTeamException()
        }

        val code =
            when (teamType) {
                TeamType.SINGLE ->
                    singleMeetingService.createMeetingTeam(userUUID, name, teamType = teamType)
                TeamType.TRIPLE ->
                    tripleMeetingService.createMeetingTeam(userUUID, name, teamType = teamType)
            }

        return ResponseEntity.status(HttpStatus.CREATED).body(code)
    }

    @Operation(summary = "미팅 팀 참가", description = "1대1의 경우 지원되지 않음. 1대1은 미팅 팀 생성 시 자동으로 참가됨")
    @ApiResponses(
        value =
            [
                ApiResponse(
                    responseCode = "204",
                    description = "isJoin true일 경우, 팀 참가 및 null 반환, false일 경우, 팀 참가하지 않고 팀 정보 반환",
                    content =
                        [
                            Content(
                                schema =
                                    Schema(implementation = MeetingTeamUserListGetResponse::class)
                            )]
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
                    description = "해당 팀에 팀장이 없음",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            value =
                                                "{message: Team Leader is not Found., status: 400, code: M16}"
                                        )]
                            )]
                ),
                ApiResponse(
                    responseCode = "400",
                    description = "팀 코드가 맞지않음",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            value =
                                                "{message: Team Code is Invalid Format., status: 400, code: M13}"
                                        )]
                            )]
                ),
                ApiResponse(
                    responseCode = "400",
                    description = "유저가 이미 팀을 가지고 있음",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            value =
                                                "{message: User already have Team., status: 400, code: M02}"
                                        )]
                            )]
                ),
                ApiResponse(
                    responseCode = "400",
                    description = "해당 팀의 정원이 꽉참",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            value =
                                                "{message: Team is Full., status: 400, code: M14}"
                                        )]
                            )]
                ),
                ApiResponse(
                    responseCode = "400",
                    description = "다른 성별의 팀에 입장 불가",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            value =
                                                "{message: Team must consist of Same Gender, status: 400, code: M17}"
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
        @RequestParam isJoin: Boolean,
    ): ResponseEntity<MeetingTeamUserListGetResponse?> {
        val userUUID = UUID.fromString(userDetails.username)

        if (teamType == TeamType.SINGLE) {
            throw InSingleMeetingTeamNoJoinTeamException()
        }

        val meetingTeamUserListGetResponse =
            tripleMeetingService.joinMeetingTeam(userUUID, code, isJoin)
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
                    description = "1대1 팀의 경우는 팀에 속한 유저 리스트 조회 불가",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                                examples =
                                    [
                                        ExampleObject(
                                            value =
                                                "{message: In Single Meeting Team, only One User Exist., status: 400, code: M05}"
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
        val userUUID = UUID.fromString(userDetails.username)

        if (teamType == TeamType.SINGLE) {
            throw InSingleMeetingTeamOnlyOneUserException()
        }

        val meetingTeamUserListGetResponse =
            tripleMeetingService.getMeetingTeamUserList(userUUID, code)

        return ResponseEntity.status(HttpStatus.OK).body(meetingTeamUserListGetResponse)
    }

    @Operation(summary = "미팅 팀 정보 기입", description = "팀의 정보를 기입함. 리더만 가능")
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
            ]
    )
    @PutMapping("/{teamType}/{isTeamLeader}/info")
    fun updateMeetingTeamInformation(
        @AuthenticationPrincipal userDetails: UserDetails,
        @PathVariable teamType: TeamType,
        @PathVariable isTeamLeader: Boolean,
        @RequestBody
        @Valid
        meetingTeamInformationUpdateRequest: MeetingTeamInformationUpdateRequest,
    ): ResponseEntity<Unit> {
        val userUUID = UUID.fromString(userDetails.username)

        if (!isTeamLeader) {
            throw OnlyTeamLeaderCanUpdateTeamInformationException()
        }

        when (teamType) {
            TeamType.SINGLE ->
                singleMeetingService.updateMeetingTeamInformation(
                    userUUID,
                    meetingTeamInformationUpdateRequest,
                )
            TeamType.TRIPLE ->
                tripleMeetingService.updateMeetingTeamInformation(
                    userUUID,
                    meetingTeamInformationUpdateRequest,
                )
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }

    @Operation(summary = "미팅 팀 상대 정보 기입", description = "팀이 원하는 상대 정보를 기입함. 리더만 가능")
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
            ]
    )
    @PutMapping("/{teamType}/{isTeamLeader}/prefer")
    fun updateMeetingTeamPreference(
        @AuthenticationPrincipal userDetails: UserDetails,
        @PathVariable teamType: TeamType,
        @PathVariable isTeamLeader: Boolean,
        @RequestBody @Valid meetingTeamPreferenceUpdateRequest: MeetingTeamPreferenceUpdateRequest,
    ): ResponseEntity<Unit> {
        val userUUID = UUID.fromString(userDetails.username)

        if (!isTeamLeader) {
            throw OnlyTeamLeaderCanUpdateTeamInformationException()
        }

        when (teamType) {
            TeamType.SINGLE ->
                singleMeetingService.updateMeetingTeamPreference(
                    userUUID,
                    meetingTeamPreferenceUpdateRequest,
                )
            TeamType.TRIPLE ->
                tripleMeetingService.updateMeetingTeamPreference(
                    userUUID,
                    meetingTeamPreferenceUpdateRequest,
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
    @GetMapping("/{teamType}/application/info")
    fun getMeetingTeamApplicationInformation(
        @AuthenticationPrincipal userDetails: UserDetails,
        @PathVariable teamType: TeamType,
    ): ResponseEntity<MeetingTeamInformationGetResponse> {
        val userUUID = UUID.fromString(userDetails.username)

        val meetingTeamInformationGetResponse =
            when (teamType) {
                TeamType.SINGLE -> singleMeetingService.getMeetingTeamInformation(userUUID)
                TeamType.TRIPLE -> tripleMeetingService.getMeetingTeamInformation(userUUID)
            }
        return ResponseEntity.status(HttpStatus.OK).body(meetingTeamInformationGetResponse)
    }

    @Operation(summary = "상대에게 보내는 메세지 입력")
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
            ]
    )
    @PutMapping("/{teamType}/{isTeamLeader}/message")
    fun updateMeetingTeamMessage(
        @AuthenticationPrincipal userDetails: UserDetails,
        @PathVariable teamType: TeamType,
        @PathVariable isTeamLeader: Boolean,
        @RequestBody @Valid meetingTeamMessageUpdateRequest: MeetingTeamMessageUpdateRequest,
    ): ResponseEntity<Unit> {
        val userUUID = UUID.fromString(userDetails.username)

        if (!isTeamLeader) {
            throw OnlyTeamLeaderCanUpdateTeamInformationException()
        }

        when (teamType) {
            TeamType.SINGLE ->
                singleMeetingService.updateMeetingTeamMessage(
                    userUUID,
                    meetingTeamMessageUpdateRequest,
                )
            TeamType.TRIPLE ->
                tripleMeetingService.updateMeetingTeamMessage(
                    userUUID,
                    meetingTeamMessageUpdateRequest,
                )
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
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
            ]
    )
    @DeleteMapping("/{teamType}/{isTeamLeader}")
    fun deleteMeetingTeam(
        @AuthenticationPrincipal userDetails: UserDetails,
        @PathVariable teamType: TeamType,
        @PathVariable isTeamLeader: Boolean,
    ): ResponseEntity<Unit> {
        val userUUID = UUID.fromString(userDetails.username)

        if (!isTeamLeader) {
            throw OnlyTeamLeaderCanDeleteTeamException()
        }

        when (teamType) {
            TeamType.SINGLE -> singleMeetingService.deleteMeetingTeam(userUUID)
            TeamType.TRIPLE -> tripleMeetingService.deleteMeetingTeam(userUUID)
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }
}
