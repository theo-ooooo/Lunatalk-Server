package kr.co.lunatalk.domain.category.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import kr.co.lunatalk.domain.category.domain.CategoryVisibility;

public record CategoryUpdateRequest(
	@NotNull(message = "변경할 이름을 작성해 주세요.")
	@Schema(description = "변경할 카테고리 이름")
	String name,
	@NotNull(message = "변경할 노출여부를 작성해 주세요.")
	@Schema(description = "변경할 노출 여부")
	CategoryVisibility visibility) {
}
