package uoslife.servermeeting.payment.api

import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import uoslife.servermeeting.global.auth.security.JwtUserDetails
import uoslife.servermeeting.meetingteam.entity.MeetingTeam
import uoslife.servermeeting.meetingteam.entity.Preference
import uoslife.servermeeting.meetingteam.entity.UserTeam
import uoslife.servermeeting.meetingteam.entity.enums.TeamType
import uoslife.servermeeting.meetingteam.repository.MeetingTeamRepository
import uoslife.servermeeting.meetingteam.repository.UserTeamRepository
import uoslife.servermeeting.user.entity.User
import uoslife.servermeeting.user.entity.enums.GenderType
import uoslife.servermeeting.user.repository.UserRepository

@SpringBootTest
@AutoConfigureMockMvc
class PaymentApiTest {

    @Autowired private lateinit var mockMvc: MockMvc

    @Autowired private lateinit var userRepository: UserRepository

    @Autowired private lateinit var meetingTeamRepository: MeetingTeamRepository

    @Autowired private lateinit var userTeamRepository: UserTeamRepository

    @BeforeEach
    fun setupSecurityContext() {
        val mockUser =
            User(
                id = 1L,
                gender = GenderType.MALE,
                email = "test@uos.ac.kr",
                phoneNumber = "01090068420",
                name = "석우진"
            )
        userRepository.save(mockUser)
        val meetingTeam =
            MeetingTeam(id = 1L, gender = GenderType.MALE, season = 5, type = TeamType.SINGLE)
        meetingTeam.preference = Preference()
        meetingTeamRepository.save(meetingTeam)
        userTeamRepository.save(UserTeam(user = mockUser, team = meetingTeam, isLeader = true))
    }

    val requestBody =
        """
        {
          "pg": "WELCOME_PAYMENTS",
          "payMethod": "CARD"
        }
    """.trimIndent(
        )

    @AfterEach
    fun clearSecurityContext() {
        SecurityContextHolder.clearContext()
    }

    @Test
    @WithMockUser(username = "1", roles = ["USER"])
    fun `1대1 팀 결제 생성`() {
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/api/payment/SINGLE/request")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody)
            )
            .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `중복 요청 방지 AOP 작동 확인`() {
        // ExecutorService로 스레드 풀을 생성
        val executorService: ExecutorService = Executors.newFixedThreadPool(2)

        // 두 개의 요청을 동시에 처리하기 위한 Callable
        val request1: Callable<MvcResult> = Callable {
            // Mock 인증 설정
            val mockUserDetails =
                JwtUserDetails(
                    id = "1", // Mock user ID
                    authorities = mutableListOf(GrantedAuthority { "ROLE_USER" })
                )
            val authentication =
                UsernamePasswordAuthenticationToken(
                    mockUserDetails,
                    null,
                    mockUserDetails.authorities
                )
            SecurityContextHolder.getContext().authentication = authentication

            // 첫 번째 요청
            mockMvc
                .perform(
                    MockMvcRequestBuilders.post("/api/payment/SINGLE/request")
                        .contentType("application/json")
                        .content(requestBody)
                )
                .andReturn() // MvcResult 반환
        }

        val request2: Callable<MvcResult> = Callable {
            // Mock 인증 설정
            val mockUserDetails =
                JwtUserDetails(
                    id = "1", // Mock user ID
                    authorities = mutableListOf(GrantedAuthority { "ROLE_USER" })
                )
            val authentication =
                UsernamePasswordAuthenticationToken(
                    mockUserDetails,
                    null,
                    mockUserDetails.authorities
                )
            SecurityContextHolder.getContext().authentication = authentication

            // 두 번째 요청
            mockMvc
                .perform(
                    MockMvcRequestBuilders.post("/api/payment/SINGLE/request")
                        .contentType("application/json")
                        .content(requestBody)
                )
                .andReturn() // MvcResult 반환
        }

        // 두 요청을 동시에 실행
        val futures = executorService.invokeAll(listOf(request1, request2))

        // 두 요청 결과를 검증
        val result1 = futures[0].get()
        val result2 = futures[1].get()

        // 상태 코드 확인
        val status1 = result1.response.status
        val status2 = result2.response.status

        assertTrue(
            (status1 == 200 && status2 == 429) || (status1 == 429 && status2 == 200),
            "두 요청 중 하나는 200 OK, 다른 하나는 429 Too Many Requests이어야 합니다."
        )
    }
}
