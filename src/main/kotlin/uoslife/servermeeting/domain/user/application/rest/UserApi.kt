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
import uoslife.servermeeting.domain.user.application.response.UserFindResponseDto
import uoslife.servermeeting.domain.user.domain.entity.User
import uoslife.servermeeting.domain.user.domain.service.UserService
import java.util.*

@Tag(name = "User", description = "User API")
@RestController
@RequestMapping("/api/user")
class UserApi(
    private val userService: UserService,
) {

    @Operation(summary = "User UUID 조회", description = "세션을 통해서 User의 정보를 조회합니다. 정보가 없는 경우 직접 User row를 생성한 후 반환합니다.")
    @GetMapping()
    fun getUser(@AuthenticationPrincipal userDetails: UserDetails): ResponseEntity<UserFindResponseDto>? {
        return userService.findUser(UUID.fromString(userDetails.username))
    }

    @Operation(summary = "User 정보 업데이트", description = "Body의 데이터를 통해서 유저의 정보를 업데이트합니다.")
    @PatchMapping("/{id}")
    fun updateUser(@RequestBody(required = false) requestBody: UserUpdateRequestDto,
                   @PathVariable id: UUID): ResponseEntity<UUID> {
        return userService.updateUser(requestBody, id)
    }
}
