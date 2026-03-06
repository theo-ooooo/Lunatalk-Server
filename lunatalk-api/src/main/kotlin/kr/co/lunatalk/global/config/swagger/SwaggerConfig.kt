package kr.co.lunatalk.global.config.swagger

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import kr.co.lunatalk.global.common.constants.UrlConstants
import kr.co.lunatalk.global.util.SpringEnvironmentUtil
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig(
    private val springEnvironmentUtil: SpringEnvironmentUtil
) {

    @Bean
    fun openAPI(): OpenAPI {
        return OpenAPI()
            .servers(swaggerServer())
            .addSecurityItem(securityRequirement())
            .components(authSetting())
            .info(swaggerInfo())
    }

    private fun swaggerServer(): List<Server> =
        listOf(Server().url(getServerUrl()).description("LUNATALK API"))

    private fun getServerUrl(): String = when (springEnvironmentUtil.getCurrentProfile()) {
        "dev" -> UrlConstants.DEV_SERVER_URL.value
        "prod" -> UrlConstants.PROD_SERVER_URL.value
        else -> UrlConstants.LOCAL_SERVER_URL.value
    }

    private fun securityRequirement(): SecurityRequirement =
        SecurityRequirement().addList("accessToken")

    private fun swaggerInfo(): Info {
        val license = License().apply {
            url = "https://github.com/theo-ooooo/Lunatalk-Server"
            name = "루나톡 서버"
        }

        return Info()
            .version("v0.0.1")
            .title("루나톡 서버 API")
            .description("루나톡 서버 API 문서")
            .license(license)
    }

    private fun authSetting(): Components =
        Components()
            .addSecuritySchemes(
                "accessToken",
                SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .`in`(SecurityScheme.In.HEADER)
                    .name("Authorization")
            )
}
