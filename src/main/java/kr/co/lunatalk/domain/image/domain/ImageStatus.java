package kr.co.lunatalk.domain.image.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ImageStatus {
	PENDING("pending"),
	COMPLETED("completed"),
	DELETED("deleted"),
	;
	private String status;
}
