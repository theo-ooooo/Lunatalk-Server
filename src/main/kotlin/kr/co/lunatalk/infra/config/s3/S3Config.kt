package kr.co.lunatalk.infra.config.s3

import kr.co.lunatalk.global.util.SpringEnvironmentUtil
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.presigner.S3Presigner

@Configuration
class S3Config(
    private val properties: S3Properties,
    private val springEnvironmentUtil: SpringEnvironmentUtil
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Bean
    fun s3Client(): S3Client {
        log.info("S3Properties : {}", properties)
        val builder = S3Client.builder().region(Region.of(properties.region))

        if (springEnvironmentUtil.isLocalProfile()) {
            builder.credentialsProvider(ProfileCredentialsProvider.create(properties.profile))
        } else {
            builder.credentialsProvider(DefaultCredentialsProvider.create())
        }

        return builder.build()
    }

    @Bean
    fun s3Presigner(): S3Presigner {
        val builder = S3Presigner.builder().region(Region.of(properties.region))

        if (springEnvironmentUtil.isLocalProfile()) {
            builder.credentialsProvider(ProfileCredentialsProvider.create(properties.profile))
        } else {
            builder.credentialsProvider(DefaultCredentialsProvider.create())
        }

        return builder.build()
    }
}
