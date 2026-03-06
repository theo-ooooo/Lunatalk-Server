package kr.co.lunatalk.infra.config.mail

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "lunatalk.mail")
data class LunatalkMailProperties(
    val from: String?,
    val adminTo: String?
)
