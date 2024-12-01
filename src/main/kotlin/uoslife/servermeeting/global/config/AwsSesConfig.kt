package uoslife.servermeeting.global.config

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AwsSesConfig(
    @Value("\${aws.access-key-id}") private val accessKeyId: String,
    @Value("\${aws.secret-access-key}") private val secretAccessKey: String,
    @Value("\${aws.region}") private val region: String
) {

    @Bean
    fun amazonSimpleEmailService(): AmazonSimpleEmailService {
        val awsCredentials = createAwsCredentials()
        return AmazonSimpleEmailServiceClientBuilder.standard()
            .withCredentials(AWSStaticCredentialsProvider(awsCredentials))
            .withRegion(region)
            .build()
    }

    private fun createAwsCredentials(): BasicAWSCredentials {
        return BasicAWSCredentials(accessKeyId, secretAccessKey)
    }
}
