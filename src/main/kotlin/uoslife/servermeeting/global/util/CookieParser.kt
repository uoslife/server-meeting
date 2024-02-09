package uoslife.servermeeting.global.util

import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Component

@Component
class CookieParser {

  fun extractCookieValue(request: HttpServletRequest, cookieName: String): String? {
    val cookies = request.cookies
    if (cookies != null) {
      for (cookie in cookies) {
        if (cookie.name == cookieName) {
          return cookie.value
        }
      }
    }
    return null
  }
}
