package kr.co.lunatalk.domain.category.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.lunatalk.domain.product.dto.response.ProductFindResponse;

import java.util.List;

public record CategoryProductResponse(
	@Schema(description = "categoryId")
	Long categoryId,
	@Schema(description = "카테고리 이름")
  	String categoryName,
	@Schema(description = "카테고리에 소속된 상품들")
  	List<ProductFindResponse> products
) {
	public static CategoryProductResponse of(Long categoryId, String categoryName, List<ProductFindResponse> products) {
		return new CategoryProductResponse(categoryId, categoryName, products);
	}
}
