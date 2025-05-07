package kr.co.lunatalk.global.config.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static io.swagger.v3.oas.models.security.SecurityScheme.*;

@Configuration
public class SwaggerConfig {
	@Bean
	public OpenAPI openAPI() {


		Info info = new Info()
			.title("Lunatalk API")
			.version("v0.0.1")
			.description("Lunatalk API");


		return new OpenAPI()
			.components(authSetting())
			.info(swaggerInfo());
	}

	private Info swaggerInfo() {
		License license = new License();
		license.setUrl("https://github.com/theo-ooooo/Lunatalk-Server");
		license.setName("루나톡 서버");

		return new Info()
			.version("v0.0.1")
			.title("루나톡 서버 API")
			.description("루나톡 서버 API 문서")
			.license(license);
	}

	private Components authSetting() {
		return new Components()
			.addSecuritySchemes(
				"access-token",
				new SecurityScheme()
					.type(Type.HTTP)
					.scheme("bearer")
					.bearerFormat("JWT")
					.in(In.HEADER)
					.name("Authorization"));
	}
}
