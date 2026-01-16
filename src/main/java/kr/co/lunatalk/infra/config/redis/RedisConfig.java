package kr.co.lunatalk.infra.config.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import java.time.Duration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RedisConfig {
	private final RedisProperties redisProperties;

	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		log.info("redisProperties : {}", redisProperties);
		RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration(redisProperties.host(), redisProperties.port());
		if(!redisProperties.password().isBlank()) {
			redisConfig.setPassword(redisProperties.password());
		}
		LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
			.commandTimeout(Duration.ofSeconds(1))
			.shutdownTimeout(Duration.ZERO)
			.build();
		return new LettuceConnectionFactory(redisConfig, clientConfig);
	}
}
