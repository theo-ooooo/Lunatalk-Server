package kr.co.lunatalk.global.common.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UrlConstants {
	PROD_SERVER_URL("https://api.lunatalk.co.kr"),
	DEV_SERVER_URL("https://dev-api.lunatalk.co.kr"),
	LOCAL_SERVER_URL("http://localhost:8080"),
	;
	private String value;
}
