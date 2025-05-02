package kr.co.lunatalk.domain.category.dto.response;

import kr.co.lunatalk.domain.category.domain.Category;

public record CategoryCreateResponse(Long categoryId) {
	public static CategoryCreateResponse of(Category category) {
		return new CategoryCreateResponse(category.getId());
	}
}
