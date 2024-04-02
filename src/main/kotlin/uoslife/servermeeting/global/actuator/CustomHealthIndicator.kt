package uoslife.servermeeting.global.actuator

import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.stereotype.Component

@Component
class CustomHealthIndicator : HealthIndicator {
    override fun health(): Health {
        val errorCode = healthCheck()
        if (errorCode != 0) {
            return Health.down().withDetail("errorCode", errorCode).build()
        }
        return Health.up().build()
    }

    private fun healthCheck(): Int {
        return 0
    }
}
