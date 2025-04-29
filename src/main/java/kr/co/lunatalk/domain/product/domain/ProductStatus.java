package kr.co.lunatalk.domain.product.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProductStatus {
	ENABLE("ENABLE"),
	DISABLE("DISABLE");
	private String value;
}
