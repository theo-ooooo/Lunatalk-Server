package kr.co.lunatalk.infra.config.mail;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "lunatalk.mail")
public record LunatalkMailProperties(
	String from,
	String adminTo
) {
}


