package uoslife.servermeeting

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@EnableFeignClients @SpringBootApplication @EnableJpaAuditing class ServerMeetingApplication

fun main(args: Array<String>) {
    runApplication<ServerMeetingApplication>(*args)
}
