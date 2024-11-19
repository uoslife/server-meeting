package uoslife.servermeeting.global.config

import java.util.concurrent.Executor
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.AsyncConfigurer
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor

@Configuration
class AsyncConfig : AsyncConfigurer {
    override fun getAsyncExecutor(): Executor {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = 5
        executor.maxPoolSize = 10
        executor.queueCapacity = 50
        executor.setThreadNamePrefix("CustomAsyncExecutor-")
        executor.initialize()
        return executor
    }
}
