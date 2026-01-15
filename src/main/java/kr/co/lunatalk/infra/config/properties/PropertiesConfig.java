package kr.co.lunatalk.infra.config.properties;

import kr.co.lunatalk.infra.config.jwt.JwtProperties;
import kr.co.lunatalk.infra.config.redis.RedisProperties;
import kr.co.lunatalk.infra.config.s3.S3Properties;
import kr.co.lunatalk.infra.config.toss.TossPaymentsProperties;
import kr.co.lunatalk.infra.mail.LunatalkMailProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties({
	JwtProperties.class,
	RedisProperties.class,
	S3Properties.class,
	TossPaymentsProperties.class,
	LunatalkMailProperties.class
})
@Configuration
public class PropertiesConfig {
}
