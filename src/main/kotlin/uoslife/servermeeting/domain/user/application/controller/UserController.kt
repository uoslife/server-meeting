package uoslife.servermeeting.domain.user.application.controller

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
import uoslife.servermeeting.domain.user.domain.service.UserService
import java.util.*

@Tag(name = "User", description = "User API")
@RestController
@RequestMapping("/api/user")
class UserController(
    private val userService: UserService,
) {

    @Operation(summary = "User UUID 조회")
    @GetMapping()
    fun getUser(@AuthenticationPrincipal userDetails: UserDetails): ResponseEntity<UUID> {
        return ResponseEntity.ok(UUID.fromString(userDetails.username))
    }

    @Operation(summary = "User 정보 업데이트")
    @PatchMapping("/{id}")
    fun updateUser(@RequestBody(required = false) requestBody: UserUpdateRequestDto, @PathVariable uuid: UUID): String {
        return userService.updateUser(requestBody, uuid)
    }
}
