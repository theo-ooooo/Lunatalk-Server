package kr.co.lunatalk.global.util;

import kr.co.lunatalk.global.common.constants.EnvironmentConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

import static kr.co.lunatalk.global.common.constants.EnvironmentConstants.Constants.*;

@Component
@RequiredArgsConstructor
public class SpringEnvironmentUtil {

	private final Environment environment;

	public String getCurrentProfile() {
		return getActiveProfiles()
			.filter(profile -> profile.equals(PROD) || profile.equals(DEV))
			.findFirst()
			.orElse(LOCAL);
	}

	public boolean getProdProfile() {
		return getActiveProfiles().anyMatch(PROD::equals);
	}

	public boolean getDevProfile() {
		return getActiveProfiles().anyMatch(DEV::equals);
	}

	public boolean getLocalProfile() {
		return getActiveProfiles().anyMatch(LOCAL::equals);
	}

	private Stream<String> getActiveProfiles() {
		return Stream.of(environment.getActiveProfiles());
	}
}
