package kr.co.lunatalk.infra.config.toss

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient

@Configuration
class TossPaymentsConfig(
    private val tossPaymentsProperties: TossPaymentsProperties
) {

    @Bean
    fun tossPaymentsRestClient(): RestClient =
        RestClient.builder()
            .baseUrl(tossPaymentsProperties.baseUrl)
            .build()
}
