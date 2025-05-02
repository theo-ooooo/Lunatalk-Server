package kr.co.lunatalk.domain.category.dto.request;

import jakarta.validation.constraints.NotNull;
import kr.co.lunatalk.domain.category.domain.CategoryVisibility;

public record CategoryCreateRequest(
	@NotNull(message = "카테고리 이름을 입력해주세요.")
	String name,

	@NotNull(message = "노출 여부를 선택해주세요.")
	CategoryVisibility visibility
) {
}
