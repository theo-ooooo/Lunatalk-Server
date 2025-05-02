package kr.co.lunatalk.domain.category.dto.response;

import kr.co.lunatalk.domain.category.domain.Category;
import kr.co.lunatalk.domain.product.domain.Product;

import java.util.List;

public record CategoryAddProductResponse(Long categoryId, List<Long> productIds) {

	public static CategoryAddProductResponse of(Category category, List<Product> products) {
		return new CategoryAddProductResponse(category.getId(), products.stream().map(Product::getId).toList());
	}
}
