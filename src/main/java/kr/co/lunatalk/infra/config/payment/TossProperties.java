package kr.co.lunatalk.infra.config.payment;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "toss")
public record TossProperties(
	String clientKey,
 	String secretKey) {

}
