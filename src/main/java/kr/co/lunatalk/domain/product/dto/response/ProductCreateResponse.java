package kr.co.lunatalk.domain.product.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.lunatalk.domain.product.domain.Product;

public record ProductCreateResponse(
	@Schema(description = "상품 ID")
	Long productId
) {
	public static ProductCreateResponse from(Product product) {
		return new ProductCreateResponse(product.getId());
	}
}
