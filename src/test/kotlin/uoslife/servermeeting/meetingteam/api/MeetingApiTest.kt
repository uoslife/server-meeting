package uoslife.servermeeting.meetingteam.api

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
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import uoslife.servermeeting.global.auth.security.JwtUserDetails
import uoslife.servermeeting.user.entity.User
import uoslife.servermeeting.user.entity.enums.GenderType
import uoslife.servermeeting.user.repository.UserRepository

@SpringBootTest
@AutoConfigureMockMvc
class MeetingTeamIntegrationTest {

    @Autowired private lateinit var mockMvc: MockMvc

    @Autowired private lateinit var userRepository: UserRepository
    @BeforeEach
    fun setupSecurityContext() {
        val mockUser = User(id = 1L, gender = GenderType.MALE, email = "test@uos.ac.kr")
        userRepository.save(mockUser)
        val mockUserDetails =
            JwtUserDetails(
                id = "1", // Mock user ID
                authorities = mutableListOf(GrantedAuthority { "ROLE_USER" })
            )
        val authentication =
            UsernamePasswordAuthenticationToken(mockUserDetails, null, mockUserDetails.authorities)
        SecurityContextHolder.getContext().authentication = authentication
    }

    @AfterEach
    fun clearSecurityContext() {
        SecurityContextHolder.clearContext()
    }

    @Test
    fun `1대1 팀 생성 통합 테스트`() {
        mockMvc
            .perform(post("/api/meeting/SINGLE/create").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.code").isEmpty)
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
                    MockMvcRequestBuilders.post("/api/meeting/SINGLE/create")
                        .contentType("application/json")
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
                    MockMvcRequestBuilders.post("/api/meeting/SINGLE/create")
                        .contentType("application/json")
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
