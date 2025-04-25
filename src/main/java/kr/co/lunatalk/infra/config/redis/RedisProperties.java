package kr.co.lunatalk.infra.config.redis;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "redis")
public record RedisProperties(String host, int port, String database, String password) {
}
