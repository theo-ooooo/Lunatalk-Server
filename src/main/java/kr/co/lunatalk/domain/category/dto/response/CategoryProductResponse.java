package kr.co.lunatalk.domain.category.dto.response;

import kr.co.lunatalk.domain.product.dto.response.ProductFindResponse;

import java.util.List;

public record CategoryProductResponse(Long categoryId,
									  String categoryName,
									  List<ProductFindResponse> products
) {
	public static CategoryProductResponse of(Long categoryId, String categoryName, List<ProductFindResponse> products) {
		return new CategoryProductResponse(categoryId, categoryName, products);
	}
}
