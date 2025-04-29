package kr.co.lunatalk.domain.product.dto.response;

import kr.co.lunatalk.domain.product.domain.Product;

public record ProductCreateResponse(Long productId) {
	public static ProductCreateResponse from(Product product) {
		return new ProductCreateResponse(product.getId());
	}
}
