package kr.co.lunatalk.infra.properties;

import kr.co.lunatalk.infra.jwt.JwtProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties({JwtProperties.class})
@Configuration
public class PropertiesConfig {
}
