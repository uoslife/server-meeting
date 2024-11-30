package uoslife.servermeeting

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.scheduling.annotation.EnableAsync

@EnableFeignClients
@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
@EnableAspectJAutoProxy
class ServerMeetingApplication

fun main(args: Array<String>) {
    runApplication<ServerMeetingApplication>(*args)
}
