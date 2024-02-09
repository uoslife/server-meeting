package uoslife.servermeeting.domain.meeting.domain.common

import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import java.util.UUID
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import uoslife.servermeeting.domain.meeting.domain.dao.UserTeamDao
import uoslife.servermeeting.domain.meeting.domain.repository.MeetingTeamRepository
import uoslife.servermeeting.domain.meeting.domain.service.impl.TripleMeetingService
import uoslife.servermeeting.domain.user.domain.entity.User
import uoslife.servermeeting.domain.user.domain.entity.UserPersonalInformation
import uoslife.servermeeting.domain.user.domain.entity.enums.DepartmentNameType
import uoslife.servermeeting.domain.user.domain.entity.enums.GenderType
import uoslife.servermeeting.domain.user.domain.entity.enums.StudentType
import uoslife.servermeeting.domain.user.domain.repository.UserRepository

@SpringBootTest
@Transactional
abstract class TripleMeetingTest {

    @Autowired protected lateinit var tripleMeetingService: TripleMeetingService

    @Autowired protected lateinit var userTeamDao: UserTeamDao

    @Autowired protected lateinit var userRepository: UserRepository

    @Autowired protected lateinit var meetingTeamRepository: MeetingTeamRepository

    @Autowired protected lateinit var entityManager: EntityManager

    @Value("\${app.season}") protected val season: Int = 0

    @BeforeEach
    fun setUp() {
        userTeamDao.deleteAll()
        meetingTeamRepository.deleteAll()
        userRepository.deleteAll()

        val userPersonalInformation =
            UserPersonalInformation(
                birthYear = 2000,
                studentType = StudentType.UNDERGRADUATE,
                department = DepartmentNameType.COMPUTER_SCIENCE,
                smoking = false,
                kakaoTalkId = "kakao",
                mbti = "00101",
                interest = "00101010",
                gender = GenderType.FEMALE,
            )

        userRepository.save(
            User(
                id = UUID.randomUUID(),
                name = "name1",
                nickname = "user@" + UUID.randomUUID().toString(),
                phoneNumber = "01012345678",
                profilePicture = "",
                userPersonalInformation = userPersonalInformation,
            )
        )

        val userPersonalInformation1 =
            UserPersonalInformation(
                department = DepartmentNameType.COMPUTER_SCIENCE,
                smoking = false,
                kakaoTalkId = "kakao",
                mbti = "00101",
                interest = "00101010",
                gender = GenderType.FEMALE,
                birthYear = 1999,
                studentType = StudentType.UNDERGRADUATE,
            )
        userRepository.save(
            User(
                id = UUID.randomUUID(),
                name = "name2",
                nickname = "user@" + UUID.randomUUID().toString(),
                phoneNumber = "01012345678",
                profilePicture = "",
                userPersonalInformation = userPersonalInformation1,
            ),
        )

        val userPersonalInformation2 =
            UserPersonalInformation(
                birthYear = 2001,
                studentType = StudentType.UNDERGRADUATE,
                department = DepartmentNameType.COMPUTER_SCIENCE,
                smoking = false,
                kakaoTalkId = "kakao",
                mbti = "00101",
                interest = "00101010",
                gender = GenderType.FEMALE,
            )

        userRepository.save(
            User(
                id = UUID.randomUUID(),
                name = "name3",
                nickname = "user@" + UUID.randomUUID().toString(),
                phoneNumber = "01012345678",
                profilePicture = "",
                userPersonalInformation = userPersonalInformation2,
            ),
        )

        val userPersonalInformation3 =
            UserPersonalInformation(
                birthYear = 2001,
                studentType = StudentType.UNDERGRADUATE,
                department = DepartmentNameType.COMPUTER_SCIENCE,
                smoking = false,
                kakaoTalkId = "kakao",
                mbti = "00101",
                interest = "00101010",
                gender = GenderType.MALE,
            )

        userRepository.save(
            User(
                id = UUID.randomUUID(),
                name = "name3",
                nickname = "user@" + UUID.randomUUID().toString(),
                phoneNumber = "01012345678",
                profilePicture = "",
                userPersonalInformation = userPersonalInformation3,
            ),
        )
    }
}
