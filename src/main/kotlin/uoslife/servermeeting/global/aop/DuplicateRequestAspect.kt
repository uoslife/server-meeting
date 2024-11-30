package uoslife.servermeeting.global.aop

import java.util.concurrent.ConcurrentHashMap
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

@Aspect
@Component
class DuplicateRequestAspect {

    private val requestMap = ConcurrentHashMap<String, Long>()
    private val expirationTime = 1000L // 중복 허용 시간 (1초)

    @Around("@annotation(uoslife.servermeeting.global.aop.PreventDuplicateRequest)")
    fun duplicateRequestCheck(joinPoint: ProceedingJoinPoint): Any {
        val request =
            (RequestContextHolder.currentRequestAttributes() as ServletRequestAttributes).request
        val httpMethod = request.method
        val requestId = "${httpMethod}:${request.requestURI}:${request.getHeader("Authorization")}"

        val currentTime = System.currentTimeMillis()
        val lastRequestTime = requestMap[requestId]

        if (lastRequestTime != null && currentTime - lastRequestTime < expirationTime) {
            throw DuplicateRequestException()
        }

        requestMap[requestId] = currentTime
        return try {
            joinPoint.proceed()
        } finally {
            requestMap.remove(requestId)
        }
    }
}
