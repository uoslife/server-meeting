package uoslife.servermeeting.user.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import java.util.UUID
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import uoslife.servermeeting.global.error.ErrorResponse
import uoslife.servermeeting.user.dto.request.UserUpdateRequest
import uoslife.servermeeting.user.dto.response.UserFindResponseDto
import uoslife.servermeeting.user.service.UserService

@Tag(name = "User", description = "User API")
@RestController
@RequestMapping("/api/user")
class UserApi(private val userService: UserService) {

    @Operation(summary = "User 정보 조회", description = "토큰을 통해서 User의 정보를 조회합니다.")
    @GetMapping
    fun getUser(
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<UserFindResponseDto> {
        return userService.findUser(UUID.fromString(userDetails.username))
    }

    @Operation(summary = "User 정보 업데이트", description = "유저의 정보를 업데이트합니다.")
    @ApiResponses(
        value =
            [
                ApiResponse(
                    responseCode = "200",
                    description = "유저 정보 업데이트 성공, 반환값 없음",
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
            ]
    )
    @PatchMapping
    fun updateUser(
        @RequestBody(required = false) requestBody: UserUpdateRequest,
        @AuthenticationPrincipal userDetails: UserDetails,
    ): ResponseEntity<Unit> {
        return userService.updateUser(requestBody, UUID.fromString(userDetails.username))
    }
}
