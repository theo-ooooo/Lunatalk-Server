package kr.co.lunatalk.domain.category.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CategoryStatus {
	ACTIVE("active"),
	DELETED("deleted"),;
	private String value;
}
