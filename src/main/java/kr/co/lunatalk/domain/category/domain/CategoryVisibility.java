package kr.co.lunatalk.domain.category.domain;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CategoryVisibility {
	VISIBLE("visible"),
	HIDDEN("hidden");
	private String value;
}
