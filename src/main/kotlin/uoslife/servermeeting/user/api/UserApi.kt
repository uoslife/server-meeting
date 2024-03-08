package uoslife.servermeeting.user.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import java.util.*
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import uoslife.servermeeting.user.dto.request.CheckUserRequest
import uoslife.servermeeting.user.dto.request.UserUpdateRequest
import uoslife.servermeeting.user.dto.response.CheckUserResponse
import uoslife.servermeeting.user.dto.response.UserFindResponseDto
import uoslife.servermeeting.user.service.UserService

@Tag(name = "User", description = "User API")
@RestController
@RequestMapping("/api/user")
class UserApi(
    private val userService: UserService,
) {

    @Operation(summary = "User 정보 조회", description = "세션을 통해서 User의 정보를 조회합니다. row가 없다면 생성합니다.")
    @GetMapping
    fun getUser(
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<UserFindResponseDto> {
        return userService.findUser(UUID.fromString(userDetails.username))
    }

    @Operation(summary = "User 정보 업데이트", description = "유저의 정보를 업데이트합니다.")
    @PatchMapping
    fun updateUser(
        @RequestBody(required = false) requestBody: UserUpdateRequest,
        @AuthenticationPrincipal userDetails: UserDetails,
    ): ResponseEntity<Unit> {
        return userService.updateUser(requestBody, UUID.fromString(userDetails.username))
    }

    @Operation(summary = "user 정보 초기화", description = "user 테이블의 정보를 원래대로 되돌립니다.")
    @PutMapping
    fun resetUser(@AuthenticationPrincipal userDetails: UserDetails): ResponseEntity<Unit> {
        return userService.resetUser(UUID.fromString(userDetails.username))
    }

    @GetMapping("/check")
    fun checkUser(
        @RequestBody @Valid checkUserRequest: CheckUserRequest
    ): ResponseEntity<CheckUserResponse> {
        return userService.checkUserByEmail(checkUserRequest.email)
    }
}
