package kr.co.lunatalk.domain.category.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CategoryAddProductRequest(
	@NotNull(message = "상품을 선택해주세요.")
	List<Long> productIds
	) {
}
