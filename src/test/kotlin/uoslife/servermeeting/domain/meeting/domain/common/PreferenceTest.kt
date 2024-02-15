package uoslife.servermeeting.domain.meeting.domain.common

import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import java.util.UUID
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import uoslife.servermeeting.domain.meeting.domain.entity.UserTeam
import uoslife.servermeeting.meetingteam.dao.UserTeamDao
import uoslife.servermeeting.meetingteam.entity.MeetingTeam
import uoslife.servermeeting.meetingteam.entity.enums.TeamType
import uoslife.servermeeting.meetingteam.repository.MeetingTeamRepository
import uoslife.servermeeting.meetingteam.service.util.MeetingServiceUtils
import uoslife.servermeeting.user.entity.User
import uoslife.servermeeting.user.repository.UserRepository

@SpringBootTest
@Transactional
abstract class PreferenceTest {
    @Autowired protected lateinit var userTeamDao: UserTeamDao

    @Autowired protected lateinit var userRepository: UserRepository

    @Autowired protected lateinit var meetingTeamRepository: MeetingTeamRepository

    @Autowired protected lateinit var meetingServiceUtils: MeetingServiceUtils

    @Autowired protected lateinit var entityManager: EntityManager

    @Value("\${app.season}") protected val season: Int = 0

    @BeforeEach
    fun setUp() {
        userTeamDao.deleteAll()
        meetingTeamRepository.deleteAll()
        userRepository.deleteAll()

        val user =
            userRepository.save(
                User(
                    id = UUID.randomUUID(),
                    name = "name",
                    nickname = "user@" + UUID.randomUUID().toString(),
                    phoneNumber = "01012345678",
                    profilePicture = "",
                ),
            )

        val meetingTeam =
            meetingTeamRepository.save(
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
