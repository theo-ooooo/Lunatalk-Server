package kr.co.lunatalk.domain.category.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.lunatalk.domain.category.domain.Category;
import kr.co.lunatalk.domain.category.domain.CategoryStatus;
import kr.co.lunatalk.domain.category.domain.CategoryVisibility;

public record CategoryResponse(
	@Schema(description = "카테고리 ID")
	Long categoryId,

	@Schema(description = "카테고리 이름")
	String categoryName,

	@Schema(description = "카테고리 상태")
	CategoryStatus status,

	@Schema(description = "카테고리 노출여부")
	CategoryVisibility visibility
) {

	public static CategoryResponse from(Category category) {
		return new CategoryResponse(category.getId(), category.getName(), category.getStatus(), category.getVisibility());
	}
}
