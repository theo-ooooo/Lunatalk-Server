package kr.co.lunatalk.infra.properties;

import kr.co.lunatalk.infra.jwt.JwtProperties;
import kr.co.lunatalk.infra.config.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties({JwtProperties.class, RedisProperties.class})
@Configuration
public class PropertiesConfig {
}
