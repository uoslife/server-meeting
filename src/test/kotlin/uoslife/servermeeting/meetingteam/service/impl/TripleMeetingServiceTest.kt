package uoslife.servermeeting.meetingteam.service.impl

import io.mockk.*
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.repository.findByIdOrNull
import uoslife.servermeeting.meetingteam.dao.UserTeamDao
import uoslife.servermeeting.meetingteam.exception.GenderNotUpdatedException
import uoslife.servermeeting.meetingteam.repository.MeetingTeamRepository
import uoslife.servermeeting.meetingteam.util.UniqueCodeGenerator
import uoslife.servermeeting.meetingteam.util.Validator
import uoslife.servermeeting.user.entity.User
import uoslife.servermeeting.user.entity.enums.GenderType
import uoslife.servermeeting.user.repository.UserRepository

class TripleMeetingServiceTest {

    @MockK lateinit var userRepository: UserRepository

    @MockK lateinit var validator: Validator

    @MockK lateinit var uniqueCodeGenerator: UniqueCodeGenerator

    @MockK lateinit var userTeamDao: UserTeamDao

    @MockK lateinit var meetingTeamRepository: MeetingTeamRepository

    lateinit var tripleMeetingService: TripleMeetingService

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        tripleMeetingService =
            TripleMeetingService(
                userRepository = userRepository,
                userTeamDao = userTeamDao,
                uniqueCodeGenerator = uniqueCodeGenerator,
                meetingTeamRepository = meetingTeamRepository,
                validator = validator,
                userTeamRepository = mockk(),
                meetingServiceUtils = mockk(),
                season = 2024
            )
    }

    @Test
    fun `createMeetingTeam should create a team and return code`() {
        // Arrange
        val user = User(id = 1L, name = "Test User", gender = GenderType.MALE)
        val generatedCode = "A123"

        every { userRepository.findByIdOrNull(1L) } returns user
        every { validator.isUserAlreadyHaveTripleTeam(user) } just Runs
        every { validator.isTeamNameInvalid(any()) } just Runs
        every { uniqueCodeGenerator.getUniqueTeamCode() } returns generatedCode
        every { meetingTeamRepository.save(any()) } answers { firstArg() }
        every { userTeamDao.saveUserTeam(any(), user, true) } just Runs

        // Act
        val response = tripleMeetingService.createMeetingTeam(1L, "Test Team")

        // Assert
        assertEquals(generatedCode, response.code)
    }

    @Test
    fun `리더 유저의 성별 안정해진 경우 예외를 발생한다`() {
        // Arrange
        val user = User(id = 1L, name = "Test User", gender = null)
        val generatedCode = "A123"

        every { userRepository.findByIdOrNull(1L) } returns user
        every { validator.isUserAlreadyHaveTripleTeam(user) } just Runs
        every { validator.isTeamNameInvalid(any()) } just Runs
        every { uniqueCodeGenerator.getUniqueTeamCode() } returns generatedCode
        every { meetingTeamRepository.save(any()) } answers { firstArg() }
        every { userTeamDao.saveUserTeam(any(), user, true) } just Runs

        // Act
        val exception =
            assertThrows<GenderNotUpdatedException> {
                tripleMeetingService.createMeetingTeam(1L, "TEST")
            }
        // Assert
        assertEquals(exception.message, "Gender is not selected.")
    }
}
