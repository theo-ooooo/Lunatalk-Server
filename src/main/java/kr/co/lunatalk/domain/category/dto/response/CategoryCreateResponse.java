package kr.co.lunatalk.domain.category.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.lunatalk.domain.category.domain.Category;

public record CategoryCreateResponse(
	@Schema(description = "추가한 categoryId")
	Long categoryId
) {
	public static CategoryCreateResponse of(Category category) {
		return new CategoryCreateResponse(category.getId());
	}
}
