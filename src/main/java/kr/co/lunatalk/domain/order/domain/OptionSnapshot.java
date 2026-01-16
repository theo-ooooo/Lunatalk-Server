package kr.co.lunatalk.domain.order.domain;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class OptionSnapshot {
	private String color;

	@Builder(access = AccessLevel.PRIVATE)
	public OptionSnapshot(String color) {
		this.color = color;
	}


	public static OptionSnapshot createOptionSnapshot(String color) {
		return OptionSnapshot.builder()
			.color(color)
			.build();
	}

}
