package kr.co.lunatalk.domain.image.dto.request;

import jakarta.validation.constraints.NotNull;

public record ProductImageCompletedRequest(
	@NotNull(message = "이미지 키는 필수 값입니다.")
	String imageKey
) {
}
