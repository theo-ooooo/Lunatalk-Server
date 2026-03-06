package kr.co.lunatalk.infra.config.toss

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "toss")
data class TossPaymentsProperties(
    val secretKey: String,
    val clientKey: String,
    val baseUrl: String
)
