package kr.co.lunatalk.global.config.security;

import kr.co.lunatalk.domain.member.domain.MemberRole;
import kr.co.lunatalk.global.common.constants.UrlConstants;
import kr.co.lunatalk.global.filter.JwtAuthenticationFilter;
import kr.co.lunatalk.global.security.JwtTokenProvider;
import kr.co.lunatalk.global.util.SpringEnvironmentUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import java.util.List;

import static org.springframework.security.config.Customizer.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {
	private final JwtTokenProvider jwtTokenProvider;
	private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
	private final CustomAccessDeniedHandler customAccessDeniedHandler;
	private final SpringEnvironmentUtil springEnvironmentUtil;
	private static final String[] SwaggerPatterns = {
		"/swagger-resources/**", "/swagger-ui/**", "/v3/api-docs/**",
	};

	@Value("${swagger.user}")
	private String swaggerUser;

	@Value("${swagger.password}")
	private String swaggerPassword;


	@Bean
	public InMemoryUserDetailsManager inMemoryUserDetailsManager(PasswordEncoder passwordEncoder) {
		UserDetails user = User
			.withUsername(swaggerUser)
			.password(passwordEncoder.encode(swaggerPassword))
			.roles("SWAGGER")
			.build();

		return new InMemoryUserDetailsManager(user);
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	@Order(1)
	public SecurityFilterChain swaggerFilterChain(HttpSecurity http) throws Exception {
		defaultFilterChain(http);

		http.securityMatcher(SwaggerPatterns).httpBasic(withDefaults());

		http.authorizeHttpRequests(
			authorize ->
				authorize.anyRequest().authenticated()
		);

		return http.build();
	}

	@Bean
	@Order(2)
	public SecurityFilterChain securityFilterChain(HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {
		defaultFilterChain(http);

		http.authorizeHttpRequests(authorize ->
				authorize
						.requestMatchers("/members/**").hasAnyRole(MemberRole.ADMIN.name(), MemberRole.USER.name())
						.anyRequest()
						.permitAll());

		http.addFilterBefore(jwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

		http.exceptionHandling(exception -> exception
			.authenticationEntryPoint(customAuthenticationEntryPoint)
			.accessDeniedHandler(customAccessDeniedHandler));

		return http.build();
	}

	private void defaultFilterChain(HttpSecurity http) throws Exception {
		// form login disable
		http.formLogin(AbstractHttpConfigurer::disable)
			.logout(AbstractHttpConfigurer::disable)
			.csrf(AbstractHttpConfigurer::disable)
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();

		if(springEnvironmentUtil.isProdProfile()) {
			configuration.addAllowedOriginPattern(UrlConstants.PROD_SERVER_URL.getValue());
		}

		if(springEnvironmentUtil.isDevProfile()) {
			configuration.addAllowedOriginPattern(UrlConstants.DEV_SERVER_URL.getValue());
		}

		configuration.addAllowedHeader("*");
		configuration.addAllowedMethod("*");
		configuration.setAllowCredentials(true);


		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);

		return source;
	}

	@Bean
	public JwtAuthenticationFilter jwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
		return new JwtAuthenticationFilter(jwtTokenProvider);
	}
}
