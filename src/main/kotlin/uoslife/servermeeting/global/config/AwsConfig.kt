package uoslife.servermeeting.global.config

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.WebIdentityTokenFileCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sesv2.SesV2Client
import software.amazon.awssdk.services.sts.StsClient
import software.amazon.awssdk.services.sts.auth.StsAssumeRoleCredentialsProvider
import software.amazon.awssdk.services.sts.model.AssumeRoleRequest

@Configuration
class AwsConfig(
    @Value("\${cloud.aws.ses.iam.role-arn}") private val sesRoleArn: String,
    @Value("\${cloud.aws.ses.iam.role-session-name}") private val sesRoleSeesionName: String,
    @Value("\${cloud.aws.ses.isLocal}") private val isLocal: Boolean,
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
    fun sesV2Client(): SesV2Client {
        if (isLocal) {
            logger.info("SES credentials is Local")

            return SesV2Client.builder().region(Region.AP_NORTHEAST_2).build()
        }

        val stsAssumeRoleCredentialsProvider = assumeRole(sesRoleArn, sesRoleSeesionName)
        logger.info("SES credentials requested")

        logger.info("${stsAssumeRoleCredentialsProvider?.resolveCredentials()?.accessKeyId()}")

        return SesV2Client.builder()
            .credentialsProvider(stsAssumeRoleCredentialsProvider)
            .region(Region.AP_NORTHEAST_2)
            .build()
    }
}
