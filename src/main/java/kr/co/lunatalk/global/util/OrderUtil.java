package kr.co.lunatalk.global.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class OrderUtil {
	private static final SecureRandom random = new SecureRandom();
	private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final String ORDER_NUMBER_PREFIX = "L";

	public String generateOrderNumber() {
		String timeStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmss"));
		long timeLong = Long.parseLong(timeStr);
		String base36Time = Long.toString(timeLong, 36).toUpperCase();
		String randomPart = generateRandomAlpha(2);

		return ORDER_NUMBER_PREFIX + base36Time + randomPart;
	}

	private String generateRandomAlpha(int length) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < length; i++) {
			sb.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
		}
		return sb.toString();
	}
}
