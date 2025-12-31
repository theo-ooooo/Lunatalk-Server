package kr.co.lunatalk.domain.inquiry.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum InquiryStatus {
	PENDING("대기중"),
	ANSWERED("답변완료"),
	CLOSED("종료");

	private final String description;
}

