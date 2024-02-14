package uoslife.servermeeting.domain.meeting.domain

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Test
import uoslife.servermeeting.meetingteam.entity.Information

class MappingTest {

    @Test
    fun `mapping test`() {
        val response =
            """
                { "meetingTime": "2021-10-09T14:00:00", 
                    "meetingPlace": "seoul",
                    "gender": "female",
                    "questions": {
                        "smoking": false,
                        "age": 24,
                        "department": "COMPUTER_SCIENCE",
                        "kakaoTalkId": "kakao"
                    }
                }
            """.trimIndent(
            )
        val information = jacksonObjectMapper().readValue(response, Information::class.java)
        println(information.questions?.keys)
    }
}
