package kr.co.lunatalk.domain.exhibition.domain;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExhibitionVisibility {
	VISIBLE("visible"),
	HIDDEN("hidden");
	private String value;
}
