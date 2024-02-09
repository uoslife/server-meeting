package uoslife.servermeeting.global.util

import java.net.URI
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.RequestEntity
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import uoslife.servermeeting.global.auth.exception.ExternalApiFailedException

@Component
class RestTemplateRequester {

  fun <T> sendRequestWithCookie(
      cookieValue: String,
      url: String,
      responseType: Class<T>,
  ): T {
    val restTemplate = RestTemplate()

    val cookieHeader = HttpHeaders()
    cookieHeader.add(HttpHeaders.COOKIE, cookieValue)

    val uri: URI = UriComponentsBuilder.fromHttpUrl(url).build().toUri()

    val requestEntity: RequestEntity<Any> =
        RequestEntity<Any>(
            cookieHeader,
            HttpMethod.GET,
            uri,
        )
    try {
      val responseEntity: ResponseEntity<T> = restTemplate.exchange(requestEntity, responseType)
      if (!responseEntity.statusCode.is2xxSuccessful) {
        throw ExternalApiFailedException()
      }
      return responseEntity.body ?: throw ExternalApiFailedException()
    } catch (ex: HttpClientErrorException) {
      throw ExternalApiFailedException()
    }
  }
}
