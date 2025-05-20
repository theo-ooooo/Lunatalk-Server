package kr.co.lunatalk.global.common.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UrlConstants {
	PROD_SERVER_URL("https://api.lunatalk.co.kr"),
	DEV_SERVER_URL("https://dev-api.lunatalk.co.kr"),
	LOCAL_SERVER_URL("http://localhost:8080"),


	PROD_DOMAIN_URL("https://www.lunatalk.co.kr"),
	PROD_DOMAIN_ADMIN_URL("https://admin.lunatalk.co.kr"),
	DEV_DOMAIN_URL("https://dev.lunatalk.co.kr"),
	DEV_DOMAIN_ADMIN_URL("https://dev-admin.lunatalk.co.kr"),
	LOCAL_DOMAIN_URL("http://localhost:5173")
	;
	private String value;
}
