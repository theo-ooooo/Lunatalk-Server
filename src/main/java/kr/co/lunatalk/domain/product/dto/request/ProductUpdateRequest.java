package kr.co.lunatalk.domain.product.dto.request;

import kr.co.lunatalk.domain.product.domain.ProductStatus;

public record ProductUpdateRequest(
	String name,
	Long price,
	Integer quantity,
	ProductStatus status
) {
}
