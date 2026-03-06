package kr.co.lunatalk.infra.config.redis

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import java.time.Duration

@Configuration
class RedisConfig(
    private val redisProperties: RedisProperties
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory {
        log.info("redisProperties : {}", redisProperties)
        val redisConfig = RedisStandaloneConfiguration(redisProperties.host, redisProperties.port)
        if (redisProperties.password.isNotBlank()) {
            redisConfig.setPassword(redisProperties.password)
        }
        val clientConfig = LettuceClientConfiguration.builder()
            .commandTimeout(Duration.ofSeconds(1))
            .shutdownTimeout(Duration.ZERO)
            .build()
        return LettuceConnectionFactory(redisConfig, clientConfig)
    }
}
