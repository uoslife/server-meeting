package uoslife.servermeeting.domain.user.service

import com.querydsl.jpa.impl.JPAQueryFactory
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import jakarta.persistence.EntityManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.annotation.Rollback
import uoslife.servermeeting.global.auth.util.CookieUtils
import uoslife.servermeeting.meetingteam.dao.UserTeamDao
import uoslife.servermeeting.meetingteam.repository.UserTeamRepository
import uoslife.servermeeting.meetingteam.service.BaseMeetingService
import uoslife.servermeeting.meetingteam.util.Validator
import uoslife.servermeeting.payment.service.PaymentService
import uoslife.servermeeting.user.command.UserCommand
import uoslife.servermeeting.user.dao.UserDao
import uoslife.servermeeting.user.entity.User
import uoslife.servermeeting.user.entity.enums.*
import uoslife.servermeeting.user.repository.UserInformationRepository
import uoslife.servermeeting.user.repository.UserRepository
import uoslife.servermeeting.user.service.UserService

@DataJpaTest(showSql = true)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(true)
internal class UserIntegrationTest
@Autowired
constructor(
    private val userRepository: UserRepository,
    private val userTeamRepository: UserTeamRepository,
    private val userInformationRepository: UserInformationRepository,
    private val entityManager: EntityManager,
) : BehaviorSpec() {

    override fun extensions() = listOf(SpringExtension)
    init {
        val jpaQueryFactory = JPAQueryFactory(entityManager)
        val userDao = UserDao(jpaQueryFactory)
        val paymentService = mockk<PaymentService>()
        val userTeamDao = mockk<UserTeamDao>()
        val meetingService = mockk<BaseMeetingService>()
        val cookieUtils = mockk<CookieUtils>()
        val userService =
            UserService(
                userRepository = userRepository,
                userInformationRepository = userInformationRepository,
                paymentService = paymentService,
                userTeamRepository = userTeamRepository,
                userDao = userDao,
                validator = Validator(),
                userTeamDao = userTeamDao,
                singleMeetingService = meetingService,
                tripleMeetingService = meetingService,
                cookieUtils = cookieUtils,
            )

        given("유저가 생성되었을때") {
            val savedUser = userService.createUserByEmail("seok@gmail.com")
            userRepository.flush()

            `when`("유저 info 업데이트 정보 요청이 들어오면") {
                then("업데이트를 한다") {
                    val firstUser = userService.updateUserInformation(initUserInformationCommand)
                    entityManager.flush()

                    firstUser.userInformation?.mbti shouldBe "INTJ"
                    firstUser.userInformation?.interest shouldBe listOf("운동", "독서")
                    firstUser.userInformation?.height shouldBe 180
                    firstUser.userInformation?.age shouldBe 25
                    firstUser.userInformation?.studentNumber shouldBe 2020830018
                    firstUser.userInformation?.department shouldBe "컴퓨터공학과"
                    firstUser.userInformation?.eyelidType shouldBe EyelidType.DOUBLE
                    firstUser.userInformation?.appearanceType shouldBe AppearanceType.ARAB
                }
            }
            `when`("유저 프로파일 업데이트 요청이 들어오면") {
                then("업데이트를 한다") {
                    val firstUser =
                        userService.updateUserPersonalInformation(initUserProfileCommand)
                    entityManager.flush()

                    firstUser.name shouldBe "석우진"
                    firstUser.phoneNumber shouldBe "010-1234-5678"
                    firstUser.kakaoTalkId shouldBe "seok"
                }
            }
            `when`("유저 정보가 이미 있을때") {
                userService.updateUserPersonalInformation(initUserProfileCommand)
                userService.updateUserInformation(initUserInformationCommand)

                then("userInformation을 업데이트한다") {
                    val firstUser =
                        userService.updateUserInformation(updateUserPersonalInformationCommand)
                    entityManager.flush()

                    firstUser.userInformation?.smoking shouldBe SmokingType.E_CIGARETTE
                    firstUser.userInformation?.mbti shouldBe "ENTP"
                    firstUser.userInformation?.interest shouldBe listOf("수영", "코딩")
                    firstUser.userInformation?.height shouldBe 185
                    firstUser.userInformation?.age shouldBe 23
                    firstUser.userInformation?.studentNumber shouldBe 2023830018
                    firstUser.userInformation?.department shouldBe "전자전기컴퓨터공학부"
                    firstUser.userInformation?.eyelidType shouldBe EyelidType.SINGLE
                    firstUser.userInformation?.appearanceType shouldBe AppearanceType.TOFU
                }
                then("userProfile을 업데이트한다") {
                    val firstUser =
                        userService.updateUserPersonalInformation(initUserProfileCommand)
                    entityManager.flush()

                    firstUser.name shouldBe "석우진"
                    firstUser.phoneNumber shouldBe "010-1234-5678"
                    firstUser.kakaoTalkId shouldBe "seok"
                }
            }
        }
    }

    companion object {
        private val users =
            listOf(
                User(name = "석우진", email = "seok@gmail.com"),
                User(name = "최동준", email = "dong@gmail.com"),
            )
        private val initUserInformationCommand =
            UserCommand.UpdateUserInformation(
                userId = 1L,
                smoking = SmokingType.FALSE,
                mbti = "INTJ",
                interest = listOf("운동", "독서"),
                height = 180,
                age = 25,
                studentNumber = 2020830018,
                department = "컴퓨터공학과",
                eyelidType = EyelidType.DOUBLE,
                appearanceType = AppearanceType.ARAB,
                studentType = StudentType.UNDERGRADUATE,
            )
        private val initUserProfileCommand =
            UserCommand.UpdateUserPersonalInformation(
                userId = 1L,
                name = "석우진",
                phoneNumber = "010-1234-5678",
                kakaoTalkId = "seok",
                gender = GenderType.MALE,
            )
        private val updateUserPersonalInformationCommand =
            UserCommand.UpdateUserInformation(
                userId = 1L,
                smoking = SmokingType.E_CIGARETTE,
                mbti = "ENTP",
                interest = listOf("수영", "코딩"),
                height = 185,
                age = 23,
                studentNumber = 2023830018,
                department = "전자전기컴퓨터공학부",
                eyelidType = EyelidType.SINGLE,
                appearanceType = AppearanceType.TOFU,
                studentType = StudentType.GRADUATE,
            )
        private val updateUserProfileCommand =
            UserCommand.UpdateUserPersonalInformation(
                userId = 1L,
                name = "석우진",
                phoneNumber = "010-9006-8420",
                kakaoTalkId = "seok",
                gender = GenderType.FEMALE,
            )
    }
}
