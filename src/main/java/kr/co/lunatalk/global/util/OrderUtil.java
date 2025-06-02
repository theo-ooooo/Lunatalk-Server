package kr.co.lunatalk.global.util;

import kr.co.lunatalk.domain.order.domain.Order;
import kr.co.lunatalk.domain.order.repository.OrderRepository;
import kr.co.lunatalk.global.exception.CustomException;
import kr.co.lunatalk.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class OrderUtil {
	private static final SecureRandom random = new SecureRandom();
	private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final String ORDER_NUMBER_PREFIX = "L";

	private final OrderRepository orderRepository;

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

	public Order getOrderByOrderId(Long OrderId) {
		return orderRepository.findById(OrderId).orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));
	}
}
