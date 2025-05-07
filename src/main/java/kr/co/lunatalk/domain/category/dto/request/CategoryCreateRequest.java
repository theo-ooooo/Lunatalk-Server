package kr.co.lunatalk.domain.category.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import kr.co.lunatalk.domain.category.domain.CategoryVisibility;

public record CategoryCreateRequest(
	@NotNull(message = "카테고리 이름을 입력해주세요.")
	@Schema(description = "카테고리 이름", example = "example")
	String name,

	@NotNull(message = "노출 여부를 선택해주세요.")
	@Schema(description = "노출 여부", example = "HIDDEN")
	CategoryVisibility visibility
) {
}
