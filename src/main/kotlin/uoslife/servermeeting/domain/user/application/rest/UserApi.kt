package uoslife.servermeeting.domain.user.application.rest

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import uoslife.servermeeting.domain.user.application.request.UserUpdateRequestDto
import uoslife.servermeeting.domain.user.application.response.NicknameCheckResponseDto
import uoslife.servermeeting.domain.user.application.response.UserFindResponseDto
import uoslife.servermeeting.domain.user.application.response.UserUpdateResponseDto
import uoslife.servermeeting.domain.user.domain.service.UserService
import java.util.*

@Tag(name = "User", description = "User API")
@RestController
@RequestMapping("/api/meeting/user")
class UserApi(
    private val userService: UserService,
) {

    @Operation(summary = "User 정보 조회", description = "세션을 통해서 User의 정보를 조회합니다. row가 없다면 생성합니다.")
    @GetMapping
    fun getUser(@AuthenticationPrincipal userDetails: UserDetails): ResponseEntity<UserFindResponseDto>? {
        return userService.findUser(UUID.fromString(userDetails.username))
    }

    @Operation(summary = "User 정보 업데이트", description = "유저의 정보를 업데이트합니다.")
    @PatchMapping("/{id}")
    fun updateUser(@RequestBody(required = false) requestBody: UserUpdateRequestDto,
                   @PathVariable id: UUID): ResponseEntity<UserUpdateResponseDto> {
        return userService.updateUser(requestBody, id)
    }

    @Operation(summary = "Nickname 중복 여부 확인", description = "nickname을 조회합니다.")
    @GetMapping("/{nickname}")
    fun getUserByUsername(@PathVariable nickname: String): ResponseEntity<NicknameCheckResponseDto> {
        return userService.findUserByNickname(nickname)
    }
}
