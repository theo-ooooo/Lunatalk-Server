package kr.co.lunatalk.domain.image.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record ProductImageCompletedRequest(
	@NotNull(message = "이미지 키는 필수 값입니다.")
	@Schema(description = "이미지 키")
	String imageKey
) {
}
