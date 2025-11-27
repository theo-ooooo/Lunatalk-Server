package kr.co.lunatalk.infra.config.toss;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@RequiredArgsConstructor
public class TossPaymentsConfig {

	private final TossPaymentsProperties tossPaymentsProperties;

	@Bean
	public RestClient tossPaymentsRestClient() {
		return RestClient.builder()
			.baseUrl(tossPaymentsProperties.baseUrl())
			.build();
	}
}


