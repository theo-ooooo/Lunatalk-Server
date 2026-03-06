package kr.co.lunatalk.infra.config.s3

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "aws")
data class S3Properties(
    val profile: String,
    val bucket: String,
    val region: String,
    val endpoint: String
)
