package kr.co.lunatalk.global.common.constants;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
public enum EnvironmentConstants {
	PROD(Constants.PROD),
	DEV(Constants.DEV),
	LOCAL(Constants.LOCAL);

	private String value;

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Constants {
		public static final String PROD = "prod";
		public static final String DEV = "dev";
		public static final String LOCAL = "local";
		public static List<String> PROD_DEV = List.of(PROD, DEV);
	}
}
