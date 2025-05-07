package kr.co.lunatalk.domain.category.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.lunatalk.domain.category.domain.Category;
import kr.co.lunatalk.domain.product.domain.Product;

import java.util.List;

public record CategoryAddProductResponse(
	@Schema(description = "추가한 categoryId")
	Long categoryId,
	@Schema(description = "추가한 productIds")
	List<Long> productIds) {

	public static CategoryAddProductResponse of(Category category, List<Product> products) {
		return new CategoryAddProductResponse(category.getId(), products.stream().map(Product::getId).toList());
	}
}
