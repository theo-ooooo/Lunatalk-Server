package kr.co.lunatalk.domain.payment.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import kr.co.lunatalk.global.exception.CustomException;
import kr.co.lunatalk.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
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

	@JsonCreator
	public static PaymentMethod from(String value) {
		for (PaymentMethod method : values()) {
			if (method.value.equals(value)) {
				return method;
			}
		}
		throw new CustomException(ErrorCode.PAYMENT_NOT_SUPPORT_METHOD);
	}
}
