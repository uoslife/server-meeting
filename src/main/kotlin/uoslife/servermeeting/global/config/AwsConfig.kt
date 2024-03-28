package uoslife.servermeeting.global.config

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider
import com.amazonaws.regions.Regions
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceAsyncClientBuilder
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.WebIdentityTokenFileCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sts.StsClient
import software.amazon.awssdk.services.sts.auth.StsAssumeRoleCredentialsProvider
import software.amazon.awssdk.services.sts.model.AssumeRoleRequest

@Configuration
class AwsConfig(
    @Value("\${cloud.aws.ses.iam.role-arn}") private val sesRoleArn: String,
    @Value("\${cloud.aws.ses.iam.role-session-name}") private val sesRoleSeesionName: String,
    @Value("\${cloud.aws.ses.access-key}") private val sesAccessKey: String,
    @Value("\${cloud.aws.ses.secret-key}") private val sesSecretKey: String,
) {
    companion object {
        private val logger = LoggerFactory.getLogger(AwsConfig::class.java)
    }

    private fun assumeRole(
        roleARN: String,
        roleSessionName: String
    ): StsAssumeRoleCredentialsProvider? {
        logger.info("assumeRole roleARN: $roleARN, roleSessionName: $roleSessionName")

        val stsClient =
            StsClient.builder()
                .region(Region.AP_NORTHEAST_2)
                .credentialsProvider(WebIdentityTokenFileCredentialsProvider.create())
                .build()

        val assumeRoleRequest =
            AssumeRoleRequest.builder().roleArn(roleARN).roleSessionName(roleSessionName).build()

        return StsAssumeRoleCredentialsProvider.builder()
            .stsClient(stsClient)
            .refreshRequest(assumeRoleRequest)
            .build()
    }

    @Bean
    fun amazonSimpleEmailService(): AmazonSimpleEmailService {
        // 추후 assume-role로 바꿀 예정 일단은 키 주입
        //        val stsAssumeRoleCredentialsProvider = assumeRole(s3RoleArn, s3RoleSessionName)
        //            logger.info("ses credentials requested")
        //
        // logger.info("${stsAssumeRoleCredentialsProvider?.resolveCredentials()?.accessKeyId()}")

        val basicAWSCredentials: BasicAWSCredentials =
            BasicAWSCredentials(sesAccessKey, sesSecretKey)
        val awsStaticCredentialsProvider: AWSStaticCredentialsProvider =
            AWSStaticCredentialsProvider(basicAWSCredentials)

        return AmazonSimpleEmailServiceAsyncClientBuilder.standard()
            .withCredentials(EnvironmentVariableCredentialsProvider())
            .withRegion(Regions.AP_NORTHEAST_2)
            .build()
    }
}
