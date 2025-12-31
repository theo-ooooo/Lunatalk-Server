package kr.co.lunatalk.domain.inquiry.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum InquiryType {
	PRODUCT("상품"),
	ORDER("주문"),
	GENERAL("일반");

	private final String description;
}

