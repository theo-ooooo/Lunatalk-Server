package kr.co.lunatalk.domain.product.domain;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProductVisibility {
	VISIBLE("visible"),
	HIDDEN("hidden");
	private String value;
}
