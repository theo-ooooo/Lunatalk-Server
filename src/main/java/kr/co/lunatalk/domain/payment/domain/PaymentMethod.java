package kr.co.lunatalk.domain.payment.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PaymentMethod {

	CARD("카드"),
	VIRTUAL_ACCOUNT("가상계좌"),
	EASY_PAY("간편결제"),
	MOBILE_PHONE("휴대폰"),
	ACCOUNT_TRANSFER("계좌이체"),
	CULTURE_GIFT_CERTIFICATE("문화상품권"),
	BOOK_GIFT_CERTIFICATE("도서문화상품권"),
	GAME_GIFT_CERTIFICATE("게임문화상품권");

	private final String value;

	PaymentMethod(String value) {
		this.value = value;
	}

	@JsonValue
	public String getValue() {
		return value;
	}

	@JsonCreator
	public static PaymentMethod from(String value) {
		for (PaymentMethod method : values()) {
			if (method.value.equals(value)) {
				return method;
			}
		}
		throw new IllegalArgumentException("Unknown payment method: " + value);
	}
}
