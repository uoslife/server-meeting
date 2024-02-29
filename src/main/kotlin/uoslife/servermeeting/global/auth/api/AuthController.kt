//package uoslife.servermeeting.global.auth.api
//
//import com.uoslife.core.auth.dto.AuthDto
//import com.uoslife.core.auth.dto.AuthDto.LoginResponseDto
//import com.uoslife.core.dto.SMSDto
//import com.uoslife.core.service.AuthService
//import io.swagger.v3.oas.annotations.tags.Tag
//import jakarta.validation.Valid
//import org.springframework.http.HttpStatus
//import org.springframework.http.ResponseEntity
//import org.springframework.web.bind.annotation.PostMapping
//import org.springframework.web.bind.annotation.RequestBody
//import org.springframework.web.bind.annotation.RequestMapping
//import org.springframework.web.bind.annotation.RestController
//import uoslife.servermeeting.global.auth.service.AuthService
//
//@RestController
//@RequestMapping("/core/auth")
//@Tag(name = "Auth", description = "Auth API")
//class AuthController(
//    private val authService: AuthService,
//) {
//
//    //    @PostMapping("/signin")
//    //    fun login(
//    //        @RequestBody @Valid checkRequest: SMSDto.SMSVerificationCheckRequest
//    //    ): ResponseEntity<LoginResponseDto> {
//    //        val loginResponseDto: LoginResponseDto = authService.login(checkRequest)
//    //        return if (loginResponseDto.userStatus == AuthDto.UserRegisterStatus.REGISTERED) {
//    //            ResponseEntity.ok(loginResponseDto)
//    //        } else {
//    //            ResponseEntity.status(HttpStatus.CONFLICT).body(loginResponseDto)
//    //        }
//    //    }
//
//    @PostMapping("/signin")
//    fun login(
//        @RequestBody @Valid checkRequest: SMSDto.SMSVerificationCheckRequest
//    ): ResponseEntity<LoginResponseDto> {
//        val loginResponseDto: LoginResponseDto = authService.login(checkRequest)
//        return if (loginResponseDto.userStatus == AuthDto.UserRegisterStatus.REGISTERED) {
//            ResponseEntity.ok(loginResponseDto)
//        } else {
//            ResponseEntity.status(HttpStatus.CONFLICT).body(loginResponseDto)
//        }
//    }
//
//    //    @PostMapping("/signup")
//    //    fun signUp(
//    //        @AuthenticationPrincipal userDetails: JwtUserDetails,
//    //        @RequestBody @Valid signUpDto: AuthDto.SignUpRequest,
//    //        @RequestParam(required = false) delete: Boolean?,
//    //    ): ResponseEntity<AuthDto.TokenResponse> {
//    //        val signUpResponse: AuthDto.TokenResponse =
//    //            authService.signUp(
//    //                userDetails.username,
//    //                delete,
//    //                signUpDto
//    //            ) // Username in temp token is phone number
//    //        return ResponseEntity.status(HttpStatus.CREATED).body(signUpResponse)
//    //    }
//
//    @PostMapping("/signup")
//    fun signUp(
//    //        @AuthenticationPrincipal userDetails: JwtUserDetails,
//    //        @RequestBody @Valid signUpDto: AuthDto.SignUpRequest,
//    //        @RequestParam(required = false) delete: Boolean?,
//    ): ResponseEntity<AuthDto.TokenResponse> {
//        val signUpResponse: AuthDto.TokenResponse =
//            authService.signUp(
//                userDetails.username,
//                delete,
//                signUpDto
//            ) // Username in temp token is phone number
//        return ResponseEntity.status(HttpStatus.CREATED).body(signUpResponse)
//    }
//}
