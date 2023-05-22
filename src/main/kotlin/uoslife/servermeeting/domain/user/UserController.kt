package uoslife.servermeeting.domain.user

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@Tag(name = "User", description = "User API")
@RequestMapping("/api/user")
@RestController
class UserController() {

    @Operation(summary = "User UUID 조회")
    @GetMapping("/profile")
    fun getUserProfile(@AuthenticationPrincipal userDetails: UserDetails): ResponseEntity<UUID> {
        return ResponseEntity.ok(UUID.fromString(userDetails.username))
    }
}
