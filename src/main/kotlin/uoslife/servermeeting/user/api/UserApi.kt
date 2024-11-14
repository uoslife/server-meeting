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
    @GetMapping("/isDuplicatedKakaoTalkId")
    fun isDuplicatedKakaoTalkId(@RequestParam kakaoTalkId: String): ResponseEntity<Boolean> {
        return ResponseEntity.ok(userService.isDuplicatedKakaoTalkId(kakaoTalkId))
    }
}
