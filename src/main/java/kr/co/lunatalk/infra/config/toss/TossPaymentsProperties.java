package kr.co.lunatalk.infra.config.toss;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "toss")
public record TossPaymentsProperties(
	String secretKey,
	String clientKey,
	String baseUrl
) {
}


