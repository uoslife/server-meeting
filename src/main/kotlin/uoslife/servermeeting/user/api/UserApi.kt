package uoslife.servermeeting.user.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
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
import uoslife.servermeeting.user.dto.request.CreateProfileRequest
import uoslife.servermeeting.user.dto.request.UserUpdateRequest
import uoslife.servermeeting.user.dto.response.UserFindResponse
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
                    content = [Content(schema = Schema(implementation = UserFindResponse::class))]
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
    ): ResponseEntity<UserFindResponse> {
        val userFindResponseDto: UserFindResponse =
            userService.findUser(userDetails.username.toLong())

        return ResponseEntity.ok().body(userFindResponseDto)
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
    @PatchMapping
    fun updateUser(
        @RequestBody(required = false) requestBody: UserUpdateRequest,
        @AuthenticationPrincipal userDetails: UserDetails,
    ): ResponseEntity<Unit> {
        userService.updateUser(requestBody, userDetails.username.toLong())

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }

    @Operation(summary = "프로필 생성", description = "사용자의 상세 프로필 정보를 생성합니다.")
    @ApiResponses(
        value =
            [
                ApiResponse(responseCode = "204", description = "프로필 생성 성공"),
                ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 데이터",
                    content = [Content(schema = Schema(implementation = ErrorResponse::class))]
                ),
                ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자",
                    content =
                        [
                            Content(
                                schema = Schema(implementation = ErrorResponse::class),
                            )]
                )]
    )
    @PostMapping("/create-profile")
    fun createProfile(
        @RequestBody() requestBody: CreateProfileRequest,
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<Unit> {
        val id = userDetails.username.toLong()
        userService.createProfile(requestBody, id)

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }
}
