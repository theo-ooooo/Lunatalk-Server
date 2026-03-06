package kr.co.lunatalk.global.config.security

import kr.co.lunatalk.domain.member.domain.MemberRole
import kr.co.lunatalk.global.common.constants.UrlConstants
import kr.co.lunatalk.global.filter.JwtAuthenticationFilter
import kr.co.lunatalk.global.security.JwtTokenProvider
import kr.co.lunatalk.global.util.SpringEnvironmentUtil
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.User
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
class WebSecurityConfig(
    private val jwtTokenProvider: JwtTokenProvider,
    private val customAuthenticationEntryPoint: CustomAuthenticationEntryPoint,
    private val customAccessDeniedHandler: CustomAccessDeniedHandler,
    private val springEnvironmentUtil: SpringEnvironmentUtil
) {
    private val log = LoggerFactory.getLogger(javaClass)

    companion object {
        private val SWAGGER_PATTERNS = arrayOf(
            "/swagger-resources/**", "/swagger-ui/**", "/v3/api-docs/**"
        )
    }

    @Value("\${swagger.user}")
    private lateinit var swaggerUser: String

    @Value("\${swagger.password}")
    private lateinit var swaggerPassword: String

    @Bean
    fun inMemoryUserDetailsManager(passwordEncoder: PasswordEncoder): InMemoryUserDetailsManager {
        val user = User
            .withUsername(swaggerUser)
            .password(passwordEncoder.encode(swaggerPassword))
            .roles("SWAGGER")
            .build()

        return InMemoryUserDetailsManager(user)
    }

    @Bean
    fun bCryptPasswordEncoder(): BCryptPasswordEncoder = BCryptPasswordEncoder()

    @Bean
    @Order(1)
    fun swaggerFilterChain(http: HttpSecurity): SecurityFilterChain {
        defaultFilterChain(http)

        http.securityMatcher(*SWAGGER_PATTERNS).httpBasic(Customizer.withDefaults())

        http.authorizeHttpRequests { authorize ->
            authorize.anyRequest().authenticated()
        }

        return http.build()
    }

    @Bean
    @Order(2)
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        defaultFilterChain(http)

        http.authorizeHttpRequests { authorize ->
            authorize
                .requestMatchers("/members/**")
                .hasAnyRole(MemberRole.ADMIN.name, MemberRole.USER.name)
                .anyRequest()
                .permitAll()
        }

        http.addFilterBefore(jwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter::class.java)

        http.exceptionHandling { exception ->
            exception
                .authenticationEntryPoint(customAuthenticationEntryPoint)
                .accessDeniedHandler(customAccessDeniedHandler)
        }

        return http.build()
    }

    private fun defaultFilterChain(http: HttpSecurity) {
        // form login disable
        http.formLogin { it.disable() }
            .logout { it.disable() }
            .csrf { it.disable() }
            .cors(Customizer.withDefaults())
            .sessionManagement { session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()

        if (springEnvironmentUtil.isProdProfile()) {
            configuration.addAllowedOriginPattern(UrlConstants.PROD_DOMAIN_URL.value)
            configuration.addAllowedOriginPattern(UrlConstants.PROD_DOMAIN_ADMIN_URL.value)
        }

        if (springEnvironmentUtil.isDevProfile()) {
            configuration.addAllowedOriginPattern(UrlConstants.DEV_DOMAIN_URL.value)
            configuration.addAllowedOriginPattern(UrlConstants.DEV_DOMAIN_ADMIN_URL.value)
            configuration.addAllowedOriginPattern(UrlConstants.LOCAL_ADMIN_DOMAIN_URL.value)
            configuration.addAllowedOriginPattern(UrlConstants.LOCAL_DOMAIN_URL.value)
        }

        if (springEnvironmentUtil.isLocalProfile()) {
            configuration.addAllowedOriginPattern(UrlConstants.LOCAL_ADMIN_DOMAIN_URL.value)
            configuration.addAllowedOriginPattern(UrlConstants.LOCAL_DOMAIN_URL.value)
        }

        configuration.addAllowedHeader("*")
        configuration.addAllowedMethod("*")
        configuration.allowCredentials = true

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)

        return source
    }

    @Bean
    fun jwtAuthenticationFilter(jwtTokenProvider: JwtTokenProvider): JwtAuthenticationFilter =
        JwtAuthenticationFilter(jwtTokenProvider)
}
