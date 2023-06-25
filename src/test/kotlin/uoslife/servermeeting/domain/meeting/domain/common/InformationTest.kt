package uoslife.servermeeting.domain.meeting.domain.common

import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import uoslife.servermeeting.domain.meeting.domain.dao.InformationUpdateDao
import uoslife.servermeeting.domain.meeting.domain.dao.UserTeamDao
import uoslife.servermeeting.domain.meeting.domain.entity.MeetingTeam
import uoslife.servermeeting.domain.meeting.domain.entity.UserTeam
import uoslife.servermeeting.domain.meeting.domain.entity.enums.TeamType
import uoslife.servermeeting.domain.meeting.domain.repository.InformationRepository
import uoslife.servermeeting.domain.meeting.domain.repository.MeetingTeamRepository
import uoslife.servermeeting.domain.meeting.domain.service.util.MeetingServiceUtils
import uoslife.servermeeting.domain.user.domain.entity.User
import uoslife.servermeeting.domain.user.domain.repository.UserRepository
import java.util.*

@SpringBootTest
@Transactional
abstract class InformationTest {
    @Autowired
    protected lateinit var userTeamDao: UserTeamDao

    @Autowired
    protected lateinit var userRepository: UserRepository

    @Autowired
    protected lateinit var meetingTeamRepository: MeetingTeamRepository

    @Autowired
    protected lateinit var informationRepository: InformationRepository

    @Autowired
    protected lateinit var informationUpdateDao: InformationUpdateDao

    @Autowired
    protected lateinit var meetingServiceUtils: MeetingServiceUtils

    @Autowired
    protected lateinit var entityManager: EntityManager

    @Value("\${app.season}")
    protected val season: Int = 0

    @BeforeEach
    fun setUp() {
        userTeamDao.deleteAll()
        meetingTeamRepository.deleteAll()
        userRepository.deleteAll()
        informationRepository.deleteAll()

        val user = userRepository.save(
            User(
                id = UUID.randomUUID(),
                name = "name",
                nickname = "user@" + UUID.randomUUID().toString(),
                phoneNumber = "01012345678",
                profilePicture = "",
            ),
        )

        val meetingTeam = meetingTeamRepository.save(
            MeetingTeam(
                season = season,
                code = "code",
                name = "name",
            ),
        )

        val newUserTeam = UserTeam.createUserTeam(meetingTeam, user, true, TeamType.SINGLE)
        userTeamDao.saveUserTeam(newUserTeam)
    }
}
