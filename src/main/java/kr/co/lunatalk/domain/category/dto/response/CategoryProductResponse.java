package kr.co.lunatalk.domain.category.dto.response;

import kr.co.lunatalk.domain.product.dto.response.ProductFindResponse;

import java.util.List;

public record CategoryProductResponse(Long CategoryId,
									  String categoryName,
									  List<ProductFindResponse> products
) {
	public static CategoryProductResponse of(Long CategoryId, String categoryName, List<ProductFindResponse> products) {
		return new CategoryProductResponse(CategoryId, categoryName, products);
	}
}
