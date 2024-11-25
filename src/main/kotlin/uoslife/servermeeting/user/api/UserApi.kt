package uoslife.servermeeting.user.api

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
import uoslife.servermeeting.user.dto.request.UserPersonalInformationUpdateRequest
import uoslife.servermeeting.user.dto.request.UserUpdateRequest
import uoslife.servermeeting.user.dto.response.UserBranchResponse
import uoslife.servermeeting.user.dto.response.UserProfileResponse
import uoslife.servermeeting.user.dto.response.UserSimpleResponse
import uoslife.servermeeting.user.service.UserService

@Tag(name = "User", description = "User API")
@RestController
@RequestMapping("/api/user")
class UserApi(
    private val userService: UserService,
) {
    @Operation(summary = "User 정보 조회", description = "토큰을 통해서 User의 정보를 조회합니다.")
    @ApiResponses(
        value =
            [
                ApiResponse(
                    responseCode = "200",
                    description = "유저 정보 조회 성공",
                    content =
                        [Content(schema = Schema(implementation = UserProfileResponse::class))]
                ),
                ApiResponse(
                    responseCode = "400",
                    description = "해당 유저 정보 없음",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                            )]
                ),
            ]
    )
    @GetMapping
    fun getUser(
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<UserSimpleResponse> {
        val userSimpleResponse =
            UserSimpleResponse.valueOf(userService.getUser(userDetails.username.toLong()))
        return ResponseEntity.ok().body(userSimpleResponse)
    }

    @GetMapping("/profile")
    fun getUserProfile(
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<UserProfileResponse> {
        val userProfileResponse: UserProfileResponse =
            UserProfileResponse.valueOf(userService.getUserProfile(userDetails.username.toLong()))

        return ResponseEntity.ok().body(userProfileResponse)
    }

    @Operation(summary = "User 정보 업데이트", description = "유저의 정보를 업데이트합니다.")
    @ApiResponses(
        value =
            [
                ApiResponse(
                    responseCode = "204",
                    description = "유저 정보 업데이트 성공",
                    content = [Content(schema = Schema(implementation = Unit::class))]
                ),
                ApiResponse(
                    responseCode = "400",
                    description = "해당 유저 정보 없음",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                            )]
                ),
            ]
    )
    @PatchMapping("/user-info")
    fun updateUser(
        @RequestBody(required = false) requestBody: UserUpdateRequest,
        @AuthenticationPrincipal userDetails: UserDetails,
    ): ResponseEntity<UserSimpleResponse> {
        val user =
            userService.updateUserInformation(
                requestBody.toUpdateUserInformationCommand(userDetails.username.toLong())
            )
        val userSimpleResponse = UserSimpleResponse.valueOf(user)
        return ResponseEntity.status(HttpStatus.OK).body(userSimpleResponse)
    }

    @PatchMapping
    fun updateUserPersonalInformation(
        @RequestBody(required = false) requestBody: UserPersonalInformationUpdateRequest,
        @AuthenticationPrincipal userDetails: UserDetails,
    ): ResponseEntity<UserProfileResponse> {
        val command =
            requestBody.toUpdateUserPersonalInformationCommand(userDetails.username.toLong())
        println(command.toString())
        val user = userService.updateUserPersonalInformation(command)
        val userProfileResponse = UserProfileResponse.valueOf(user)
        return ResponseEntity.status(HttpStatus.OK).body(userProfileResponse)
    }

    @Operation(summary = "User 계정 삭제", description = "유저 ID를 이용하여 삭제합니다.")
    @ApiResponses(
        value =
            [
                ApiResponse(
                    responseCode = "204",
                    description = "유저 삭제 성공",
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
                                                "{message: User is not Found., status:400, code: U02}"
                                        )]
                            )]
                )]
    )
    @DeleteMapping("/{userId}")
    fun deleteUserById(@PathVariable("userId") userId: Long): ResponseEntity<Unit> {
        userService.deleteUserById(userId)

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }

    @Operation(summary = "User 계정 삭제", description = "토큰을 이용하여 삭제합니다.")
    @ApiResponses(
        value =
            [
                ApiResponse(
                    responseCode = "204",
                    description = "유저 삭제 성공",
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
                                                "{message: User is not Found., status:400, code: U02}"
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
    @DeleteMapping()
    fun deleteUserByToken(@AuthenticationPrincipal userDetails: UserDetails): ResponseEntity<Unit> {
        val userId: Long = userDetails.username.toLong()
        userService.deleteUserById(userId)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }

    @Operation(summary = "카카오톡 아이디 중복 확인", description = "카카오톡 아이디 중복 확인합니다.")
    @ApiResponses(
        value =
            [
                ApiResponse(
                    responseCode = "200",
                    description = "카카오톡 아이디 중복 결과값",
                    content = [Content(schema = Schema(implementation = Boolean::class))]
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
                                                "{message: User is not Found., status:400, code: U02}"
                                        )]
                            )]
                )]
    )
    @GetMapping("/check/kakao-talk-id")
    fun isDuplicatedKakaoTalkId(@RequestParam kakaoTalkId: String): ResponseEntity<Boolean> {
        return ResponseEntity.ok(userService.isDuplicatedKakaoTalkId(kakaoTalkId))
    }

    @Operation(summary = "유저 미팅팀 별 기본 정보", description = "유저의 1:1, 3:3팀의 현재 상태를 요약합니다")
    @ApiResponses(
        value =
            [
                ApiResponse(
                    responseCode = "200",
                    description = "미팅팀 상태 정보",
                    content = [Content(schema = Schema(implementation = UserBranchResponse::class))]
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
                                                "{message: User is not Found., status:400, code: U02}"
                                        )]
                            )]
                )]
    )
    @GetMapping("/status")
    fun getUserMeetingTeamStatus(
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<UserBranchResponse> {
        val userId = userDetails.username.toLong()
        return ResponseEntity.ok(userService.getUserMeetingTeamBranch(userId))
    }
}
