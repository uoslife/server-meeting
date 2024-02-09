package uoslife.servermeeting

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication @EnableJpaAuditing class ServerMeetingApplication

fun main(args: Array<String>) {
    runApplication<ServerMeetingApplication>(*args)
}
