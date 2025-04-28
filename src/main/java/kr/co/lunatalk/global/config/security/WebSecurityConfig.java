package kr.co.lunatalk.global.config.security;

import kr.co.lunatalk.global.filter.JwtAuthenticationFilter;
import kr.co.lunatalk.global.security.JwtTokenProvider;
import kr.co.lunatalk.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
	private final JwtTokenProvider jwtTokenProvider;

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain config(HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {
		// form login disable
		http.formLogin(AbstractHttpConfigurer::disable)
			.logout(AbstractHttpConfigurer::disable)
			.csrf(AbstractHttpConfigurer::disable)
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		http.authorizeHttpRequests(authorize ->
			authorize
				.requestMatchers("/members/**")
				.authenticated()
				.anyRequest()
				.permitAll());

		http.addFilterBefore(jwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public JwtAuthenticationFilter jwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
		return new JwtAuthenticationFilter(jwtTokenProvider);
	}
}
