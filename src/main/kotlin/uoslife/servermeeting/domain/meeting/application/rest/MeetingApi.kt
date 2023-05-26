package uoslife.servermeeting.domain.meeting.application.rest

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import uoslife.servermeeting.domain.meeting.application.request.MeetingTeamInformationUpdateRequest
import uoslife.servermeeting.domain.meeting.application.response.MeetingTeamInformationGetResponse
import uoslife.servermeeting.domain.meeting.application.response.MeetingTeamUserListGetResponse
import uoslife.servermeeting.domain.meeting.domain.dao.UserTeamDao
import uoslife.servermeeting.domain.meeting.domain.entity.enums.TeamType
import uoslife.servermeeting.domain.meeting.domain.exception.*
import uoslife.servermeeting.domain.meeting.domain.service.BaseMeetingService
import java.util.*

@RestController
@RequestMapping("/api/meeting")
@Tag(name = "Meeting", description = "미팅 API")
class MeetingApi(
    @Qualifier("singleMeetingService") private val singleMeetingService: BaseMeetingService,
    @Qualifier("tripleMeetingService") private val tripleMeetingService: BaseMeetingService,
    private val userTeamDao: UserTeamDao,
) {

    @Operation(summary = "미팅 팀 생성", description = "리더만 팀 생성 가능")
    @ApiResponse(
        responseCode = "201",
        description = "1대1의 경우 빈 문자열을 반환, 3대3의 경우 팀 코드(A-Z0-9 4개)(String)를 반환",
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

        val code = when (teamType) {
            TeamType.SINGLE -> singleMeetingService.createMeetingTeam(userUUID, name)
            TeamType.TRIPLE -> tripleMeetingService.createMeetingTeam(userUUID, name)
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(code)
    }

    @Operation(summary = "미팅 팀 참가", description = "1대1의 경우 지원되지 않음. 1대1은 미팅 팀 생성 시 자동으로 참가됨")
    @ApiResponse(responseCode = "204", description = "반환값 없음")
    @PostMapping("/{teamType}/join/{code}")
    fun joinMeetingTeam(
        @AuthenticationPrincipal userDetails: UserDetails,
        @PathVariable teamType: TeamType,
        @PathVariable code: String,
    ): ResponseEntity<Unit> {
        val userUUID = UUID.fromString(userDetails.username)

        if (teamType == TeamType.SINGLE) {
            throw InSingleMeetingTeamNoJoinTeamException()
        }

        tripleMeetingService.joinMeetingTeam(userUUID, code)

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }

    @Operation(summary = "팅 결성 대기 중 간에 미팅 팀 유저 리스트 조회", description = "1대1의 경우 지원되지 않음. 1대1은 팀 유저가 본인 단독")
    @ApiResponse(
        responseCode = "200",
        description = "미팅 팀 유저 리스트 및 팀 이름(MeetingTeamUserListGetResponse) 반환",
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

        val meetingTeamUserListGetResponse = tripleMeetingService.getMeetingTeamUserList(userUUID, code)

        return ResponseEntity.status(HttpStatus.OK).body(meetingTeamUserListGetResponse)
    }

    @Operation(summary = "미팅 팀 정보 기입", description = "팀의 정보를 기입함. 리더만 가능")
    @ApiResponse(responseCode = "204", description = "반환값 없음")
    @PostMapping("/{teamType}/{isTeamLeader}/info")
    fun updateMeetingTeamInformation(
        @AuthenticationPrincipal userDetails: UserDetails,
        @PathVariable teamType: TeamType,
        @PathVariable isTeamLeader: Boolean,
        @RequestBody @Valid
        meetingTeamInformationUpdateRequest: MeetingTeamInformationUpdateRequest,
    ): ResponseEntity<Unit> {
        val userUUID = UUID.fromString(userDetails.username)

        if (!isTeamLeader) {
            throw OnlyTeamLeaderCanUpdateTeamInformationException()
        }

        when (teamType) {
            TeamType.SINGLE -> singleMeetingService.updateMeetingTeamInformation(
                userUUID,
                meetingTeamInformationUpdateRequest.informationDistance,
                meetingTeamInformationUpdateRequest.informationFilter,
                meetingTeamInformationUpdateRequest.informationMeetingTime,
                meetingTeamInformationUpdateRequest.preferenceDistance,
                meetingTeamInformationUpdateRequest.preferenceFilter,
            )

            TeamType.TRIPLE -> tripleMeetingService.updateMeetingTeamInformation(
                userUUID,
                meetingTeamInformationUpdateRequest.informationDistance,
                meetingTeamInformationUpdateRequest.informationFilter,
                meetingTeamInformationUpdateRequest.informationMeetingTime,
                meetingTeamInformationUpdateRequest.preferenceDistance,
                meetingTeamInformationUpdateRequest.preferenceFilter,
            )
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }

    @Operation(summary = "미팅 팀 전체 정보 조회")
    @ApiResponse(
        responseCode = "200",
        description = "미팅 팀 전체 정보(MeetingTeamInformationGetResponse) 반환",
    )
    @GetMapping("/{teamType}/application/info")
    fun getMeetingTeamApplicationInformation(
        @AuthenticationPrincipal userDetails: UserDetails,
        @PathVariable teamType: TeamType,
    ): ResponseEntity<MeetingTeamInformationGetResponse> {
        val userUUID = UUID.fromString(userDetails.username)

        val meetingTeamInformationGetResponse = when (teamType) {
            TeamType.SINGLE -> singleMeetingService.getMeetingTeamInformation(userUUID)
            TeamType.TRIPLE -> tripleMeetingService.getMeetingTeamInformation(userUUID)
        }
        return ResponseEntity.status(HttpStatus.OK).body(meetingTeamInformationGetResponse)
    }

    @Operation(summary = "미팅 팀 삭제", description = "리더만 팀 삭제 가능")
    @ApiResponse(responseCode = "204", description = "반환값 없음")
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
