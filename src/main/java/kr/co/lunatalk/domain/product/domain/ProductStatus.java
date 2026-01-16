package kr.co.lunatalk.domain.product.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProductStatus {
	ACTIVE("active"),
	DELETED("deleted"),;
	private String value;
}
