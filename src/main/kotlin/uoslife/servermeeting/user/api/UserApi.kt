package uoslife.servermeeting.user.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import java.util.UUID
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import uoslife.servermeeting.user.dto.request.TosDto
import uoslife.servermeeting.user.dto.request.UserUpdateRequest
import uoslife.servermeeting.user.dto.response.UserFindResponseDto
import uoslife.servermeeting.user.repository.UserRepository
import uoslife.servermeeting.user.service.UserService

@Tag(name = "User", description = "User API")
@RestController
@RequestMapping("/api/user")
class UserApi(private val userService: UserService, private val userRepository: UserRepository) {

    @Operation(summary = "User 정보 조회", description = "토큰을 통해서 User의 정보를 조회합니다. row가 없다면 생성합니다.")
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

    @PostMapping("/tos")
    fun setTos(@AuthenticationPrincipal userDetails: UserDetails, @RequestBody @Valid tosDto: TosDto): ResponseEntity<Unit> {
        val userUUID: UUID = UUID.fromString(userDetails.username)

        userService.setTos(userUUID, tosDto)

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }
}
