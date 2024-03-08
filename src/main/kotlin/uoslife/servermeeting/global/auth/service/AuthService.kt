//package uoslife.servermeeting.global.auth.service
//
//import jakarta.servlet.http.HttpServletRequest
//import java.util.UUID
//import org.slf4j.LoggerFactory
//import org.springframework.data.repository.findByIdOrNull
//import org.springframework.stereotype.Service
//import org.springframework.transaction.annotation.Transactional
//import uoslife.servermeeting.user.repository.UserRepository
//import uoslife.servermeeting.user.service.UserService
//
//@Service
//@Transactional(readOnly = true)
//class AuthService(
//    private val userRepository: UserRepository,
//    private val userService: UserService,
//    private val tokenProvider: TokenProvider,
//    private val jwtUserDetailsService: JwtUserDetailsService,
//    private val tosRepository: TosRepository,
//    private val notificationService: NotificationService,
//) {
//    companion object {
//        private val logger = LoggerFactory.getLogger(AuthService::class.java)
//    }
//
//    /**
//     * @param checkRequest: SMSVerificationCheckRequest
//     * @return LoginResponseDto
//     * @throws SMSVerificationNotVerifiedException
//     * @throws UserNotFoundException
//     *
//     * 로그인 요청을 처리하는 함수입니다. SMS 인증을 통해 로그인을 진행합니다. SMS 인증이 완료되면, 해당 유저가 존재하는지 확인하고, 존재한다면 토큰을 발급합니다.
//     * 유저가 만약 삭제되었거나 존재하지 않는다면, 번호가 저장되어 있는 임시토큰을 발급합니다.
//     */
//    @Transactional
//    fun login(checkRequest: SMSDto.SMSVerificationCheckRequest): AuthDto.LoginResponseDto {
//        logger.debug(
//            "login request user mobile number: ${checkRequest.mobile.substring(0, 3)}****${checkRequest.mobile.substring(7)}"
//        )
//        val checkResponse = smsVerificationCheckResponse(checkRequest)
//
//        return handleLogin(checkResponse.mobile)
//    }
//
//    private fun handleLogin(mobile: String): AuthDto.LoginResponseDto {
//        val user: User? = userRepository.findUserByPhone(mobile)
//
//        return user?.let { handleExistingUser(it) } ?: handleNewUser(mobile)
//    }
//
//    private fun handleExistingUser(user: User): AuthDto.LoginResponseDto {
//        return if (user.deletedAt == null) {
//            logger.info("login user: ${user.id}")
//            val (access, refresh) = getTokenByUser(user)
//            userService.updateLoginAt(user)
//
//            AuthDto.LoginResponseDto(
//                token =
//                    AuthDto.TokenResponse(
//                        accessToken = access,
//                        refreshToken = refresh,
//                    ),
//                userStatus = AuthDto.UserRegisterStatus.REGISTERED
//            )
//        } else {
//            AuthDto.LoginResponseDto(
//                token =
//                    AuthDto.TokenResponse(
//                        tempToken = tokenProvider.generateTempTokenFromSMSVerification(user.phone),
//                    ),
//                userStatus = AuthDto.UserRegisterStatus.DELETED
//            )
//        }
//    }
//
//    private fun handleNewUser(mobile: String): AuthDto.LoginResponseDto {
//        val existsAccount: List<UserExistsAccountResponse>? = getUserExistsAccountResponses(mobile)
//        val tempToken = tokenProvider.generateTempTokenFromSMSVerification(mobile)
//
//        return AuthDto.LoginResponseDto(
//            token =
//                AuthDto.TokenResponse(
//                    tempToken = tempToken,
//                ),
//            migrationUserInfo = existsAccount,
//            userStatus =
//                if (existsAccount.isNullOrEmpty()) {
//                    AuthDto.UserRegisterStatus.NOT_REGISTERED
//                } else {
//                    AuthDto.UserRegisterStatus.MIGRATION_NEEDED
//                }
//        )
//    }
//
//    private fun smsVerificationCheckResponse(
//        checkRequest: SMSDto.SMSVerificationCheckRequest
//    ): SMSDto.SMSVerificationCheckResponse {
//        val checkResponse = smsService.checkVerificationCode(checkRequest)
//
//        if (!checkResponse.isVerified) {
//            throw SMSVerificationNotVerifiedException()
//        }
//        return checkResponse
//    }
//
//    private fun getUserExistsAccountResponses(mobile: String) =
//        try {
//            userService.getExistsAccount(mobile)
//        } catch (e: Exception) {
//            null
//        }
//
//    private fun getTokenByUser(user: User): Pair<String, String> {
//        val deviceSecret: String = UUID.randomUUID().toString()
//        user.device = deviceService.upsertDeviceSecret(user, deviceSecret)
//        val userDetails = jwtUserDetailsService.loadUserByUsername(user.id!!.toString())
//        return Pair(
//            tokenProvider.generateAccessTokenFromUserPrincipal(userDetails),
//            tokenProvider.generateRefreshTokenFromUserPrincipal(
//                userDetails,
//                deviceSecret,
//                user.device!!.id!!
//            )
//        )
//    }
//
//    /**
//     * 3가지 종류의 유저 가입이다.
//     * 1. 새로운 유저
//     * 2. 마이그래이션 유저
//     * 3. 탈퇴 유저
//     *
//     * 새로운 유저는 닉네임 중복과 번호 중복을 체크한다. 마이그레이션 유저는 번호 중복을 체크하고 닉네임 중복 검사시 기존 서버에서의 닉네임은 검사하지 않는다. 탈퇴유저는
//     * 기존에 있는 유저를 불러와야하고 닉네임 중복검사와 번호 중복을 체크한다.
//     */
//    @Transactional
//    fun signUp(
//        mobile: String?,
//        delete: Boolean?,
//        signUpRequest: AuthDto.SignUpRequest
//    ): AuthDto.TokenResponse {
//        val existingUser =
//            userRepository.findUserByPhone(mobile ?: throw SMSVerificationNotVerifiedException())
//
//        return when {
//            existingUser?.deletedAt != null ->
//                handleSignUpForWithdrawnUser(existingUser, signUpRequest, delete)
//            signUpRequest.migrationUserInfo != null ->
//                handleSignUpForMigrationUser(mobile, signUpRequest)
//            else -> handleSignUpForNewUser(mobile, signUpRequest)
//        }
//    }
//
//    private fun handleSignUpForDeleteAndRejoinRequestUser(
//        existingUser: User,
//        signUpRequest: AuthDto.SignUpRequest
//    ): AuthDto.TokenResponse {
//        try {
//            val signUpPhoneNumber = existingUser.phone
//            existingUser.phone = "080${signUpPhoneNumber.substring(3)}"
//            return handleSignUpForNewUser(signUpPhoneNumber, signUpRequest)
//        } catch (e: Exception) {
//            logger.error("delete and rejoin error: ${e.message}")
//            throw UserRejoinLimitExceedException()
//        }
//    }
//
//    private fun handleSignUpForWithdrawnUser(
//        existingUser: User,
//        signUpRequest: AuthDto.SignUpRequest,
//        delete: Boolean?
//    ): AuthDto.TokenResponse {
//        logger.info("sign up for withdrawn user: ${existingUser.id}")
//
//        if (delete == true) {
//            return handleSignUpForDeleteAndRejoinRequestUser(existingUser, signUpRequest)
//        }
//        checkNicknameDuplication(signUpRequest.nickname)
//
//        existingUser.nickname = signUpRequest.nickname
//        existingUser.deletedAt = null
//        userService.updateLoginAt(existingUser)
//
//        return getTokenResponse(existingUser)
//    }
//
//    private fun handleSignUpForMigrationUser(
//        mobile: String,
//        signUpRequest: AuthDto.SignUpRequest
//    ): AuthDto.TokenResponse {
//        logger.info(
//            "sign up for migration user: ${mobile.substring(0, 3)}****${mobile.substring(7)}"
//        )
//        val migrationInfo = signUpRequest.migrationUserInfo ?: throw UserNotAllowedForMigration()
//
//        userService.migrateUser(UserMigrationRequest(migrationInfo.id!!))
//
//        if (migrationInfo.nickname != signUpRequest.nickname) {
//            return handleSignUpForNewUser(mobile, signUpRequest)
//        }
//
//        if (userRepository.existsByNickname(signUpRequest.nickname)) {
//            throw RequestUserNicknameDuplicatedException()
//        }
//
//        return initializeUser(mobile, signUpRequest)
//    }
//
//    private fun handleSignUpForNewUser(
//        mobile: String,
//        signUpRequest: AuthDto.SignUpRequest,
//    ): AuthDto.TokenResponse {
//        checkNicknameDuplication(signUpRequest.nickname)
//
//        return initializeUser(mobile, signUpRequest)
//    }
//
//    private fun initializeUser(
//        mobile: String,
//        signUpRequest: AuthDto.SignUpRequest
//    ): AuthDto.TokenResponse {
//        logger.info("sign up for new user: ${mobile.substring(0, 3)}****${mobile.substring(7)}")
//        val newUser = userRepository.save(User(phone = mobile, nickname = signUpRequest.nickname))
//        setUserTos(signUpRequest.tos, newUser)
//
//        return getTokenResponse(newUser)
//    }
//
//    private fun checkNicknameDuplication(nickname: String) {
//        if (userService.checkNicknameDuplication(NicknameDuplicationRequest(nickname)).duplicate) {
//            throw RequestUserNicknameDuplicatedException()
//        }
//    }
//
//    private fun getTokenResponse(user: User): AuthDto.TokenResponse {
//        val (access, refresh) = getTokenByUser(user)
//        logger.debug("sign up user: ${user.id}")
//        return AuthDto.TokenResponse(accessToken = access, refreshToken = refresh)
//    }
//
//    @Transactional
//    fun refreshAccessToken(request: HttpServletRequest): AuthDto.TokenResponse {
//        val refreshToken = tokenProvider.resolveToken(request) ?: throw InvalidTokenException()
//        val claims = tokenProvider.parseClaims(refreshToken, REFRESH_SECRET)
//
//        if (
//            !deviceService.checkDeviceSecret(
//                userId = claims.subject,
//                deviceId = claims.issuer,
//                deviceSecret = claims.audience
//            )
//        ) {
//            throw UserDeviceNotMatchedWithTokenException()
//        }
//
//        logger.debug("refresh access token for user: ${claims.subject}")
//
//        val user =
//            userRepository.findByIdOrNull(claims.subject.toLong()) ?: throw UserNotFoundException()
//        val (newAccessToken, newRefreshToken) = getTokenByUser(user)
//
//        userService.updateLoginAt(user)
//
//        return AuthDto.TokenResponse(
//            accessToken = newAccessToken,
//            refreshToken = newRefreshToken,
//        )
//    }
//
//    @Transactional
//    fun logout(userId: Long) {
//        logger.info("logout user: $userId")
//        val logoutUser = userRepository.findByIdOrNull(userId) ?: throw UserNotFoundException()
//        deviceService.deleteDeviceSecret(logoutUser.device?.id ?: throw UnauthorizedException())
//        smsVerificationRedisRepository.deleteById(logoutUser.phone)
//    }
//
//    @Transactional
//    fun unregisterUser(userId: Long) {
//        logger.info("unregister user: $userId")
//        val user = userRepository.findByIdOrNull(userId) ?: throw UserNotFoundException()
//        deleteUserDeviceIfExists(user)
//        deleteUserTosIfExists(user)
//        deleteUserPortalAccountIfExists(user)
//        userRepository.delete(user)
//    }
//
//    private fun deleteUserPortalAccountIfExists(user: User) {
//        user.portalAccount?.let { portalAccountRepository.delete(it) }
//    }
//
//    private fun deleteUserTosIfExists(user: User) {
//        user.tos?.let { tosRepository.delete(it) }
//    }
//
//    private fun deleteUserDeviceIfExists(user: User) {
//        user.device.let {
//            deviceService.deleteDeviceSecret(it?.id ?: throw UnauthorizedException())
//        }
//    }
//
//    private fun setUserTos(tosDto: TosDto, user: User?) {
//        user?.tos =
//            tosRepository.save(
//                Tos(
//                    privatePolicy = tosDto.privatePolicy ?: false,
//                    termsOfUse = tosDto.termsOfUse ?: false,
//                    notification = tosDto.notification ?: false,
//                    marketing = tosDto.marketing ?: false,
//                )
//            )
//
//        tosDto
//            .takeIf { it.marketing == true }
//            ?.let { subscribeToTopic(user, TopicName.MARKETING_NOTIFICATION.name) }
//
//        tosDto.takeIf { it.notification == true }?.let { subscribeToAnnouncementNotification(user) }
//    }
//
//    private fun subscribeToAnnouncementNotification(user: User?) {
//        subscribeToTopic(user, TopicName.SERVICE_NOTIFICATION.name)
//        subscribeToTopic(user, TopicName.GENERAL_ANNOUNCEMENT.name)
//        subscribeToTopic(user, TopicName.ACADEMIC_ANNOUNCEMENT.name)
//        subscribeToTopic(user, TopicName.RECRUIT_ANNOUNCEMENT.name)
//        subscribeToTopic(user, TopicName.STARTUP_ANNOUNCEMENT.name)
//    }
//
//    private fun subscribeToTopic(user: User?, topicName: String) {
//        notificationService.subscribeDeviceTokenToTopic(
//            userId = user?.id ?: throw UserNotFoundException(),
//            topicSubscriptionRequest =
//                NotificationDto.TopicSubscriptionRequest(topicName = topicName)
//        )
//    }
//}
