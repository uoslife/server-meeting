package uoslife.servermeeting.global.config

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AwsSesConfig {

    @Value("\${aws.access-key-id}") private lateinit var accessKeyId: String

    @Value("\${aws.secret-access-key}") private lateinit var secretAccessKey: String

    @Value("\${aws.region}") private lateinit var region: String

    @Bean
    fun amazonSimpleEmailService(): AmazonSimpleEmailService {
        val credentials = BasicAWSCredentials(accessKeyId, secretAccessKey)
        return AmazonSimpleEmailServiceClientBuilder.standard()
            .withCredentials(AWSStaticCredentialsProvider(credentials))
            .withRegion(region)
            .build()
    }
}
