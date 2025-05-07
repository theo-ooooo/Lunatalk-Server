package kr.co.lunatalk.global.config.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import kr.co.lunatalk.global.common.constants.UrlConstants;
import kr.co.lunatalk.global.util.SpringEnvironmentUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static io.swagger.v3.oas.models.security.SecurityScheme.*;

@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {

	private final SpringEnvironmentUtil springEnvironmentUtil;

	@Bean
	public OpenAPI openAPI() {


		Info info = new Info()
			.title("Lunatalk API")
			.version("v0.0.1")
			.description("Lunatalk API");


		return new OpenAPI()
				.servers(swaggerServer())
			.addSecurityItem(securityRequirement())
			.components(authSetting())
			.info(swaggerInfo());
	}

	private List<Server> swaggerServer() {
		return List.of(new Server().url(getServerUrl()).description("LUNATALK API"));
	}


	private String getServerUrl() {
		return switch (springEnvironmentUtil.getCurrentProfile()) {
			case "dev" -> UrlConstants.DEV_SERVER_URL.getValue();
			case "prod" -> UrlConstants.PROD_SERVER_URL.getValue();
			default -> UrlConstants.LOCAL_SERVER_URL.getValue();
		};
	}

	private SecurityRequirement securityRequirement() {
		return new SecurityRequirement().addList("accessToken");
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
				"accessToken",
				new SecurityScheme()
					.type(Type.HTTP)
					.scheme("bearer")
					.bearerFormat("JWT")
					.in(In.HEADER)
					.name("Authorization"));
	}
}
