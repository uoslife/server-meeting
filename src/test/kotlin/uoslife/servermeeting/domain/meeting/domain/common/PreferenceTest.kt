package uoslife.servermeeting.domain.meeting.domain.common

import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import uoslife.servermeeting.domain.meeting.domain.dao.PreferenceUpdateDao
import uoslife.servermeeting.domain.meeting.domain.dao.UserTeamDao
import uoslife.servermeeting.domain.meeting.domain.entity.MeetingTeam
import uoslife.servermeeting.domain.meeting.domain.entity.enums.TeamType
import uoslife.servermeeting.domain.meeting.domain.repository.MeetingTeamRepository
import uoslife.servermeeting.domain.meeting.domain.repository.PreferenceRepository
import uoslife.servermeeting.domain.user.domain.entity.User
import uoslife.servermeeting.domain.user.domain.repository.UserRepository
import java.util.*

@SpringBootTest
@Transactional
abstract class PreferenceTest {
    @Autowired
    protected lateinit var userTeamDao: UserTeamDao

    @Autowired
    protected lateinit var userRepository: UserRepository

    @Autowired
    protected lateinit var meetingTeamRepository: MeetingTeamRepository

    @Autowired
    protected lateinit var preferenceRepository: PreferenceRepository

    @Autowired
    protected lateinit var preferenceUpdateDao: PreferenceUpdateDao

    @Autowired
    protected lateinit var entityManager: EntityManager

    @Value("\${app.season}")
    protected val season: Int = 0

    @BeforeEach
    fun setUp() {
        userTeamDao.deleteAll()
        meetingTeamRepository.deleteAll()
        userRepository.deleteAll()
        preferenceRepository.deleteAll()

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

        userTeamDao.saveUserTeam(meetingTeam, user, true, TeamType.SINGLE)
    }
}