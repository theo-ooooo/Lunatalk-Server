package kr.co.lunatalk.domain.product.dto;

import kr.co.lunatalk.domain.category.domain.Category;
import kr.co.lunatalk.domain.category.dto.response.CategoryResponse;
import kr.co.lunatalk.domain.image.domain.Image;
import kr.co.lunatalk.domain.product.domain.Product;
import kr.co.lunatalk.domain.product.domain.ProductColor;
import kr.co.lunatalk.domain.product.domain.ProductVisibility;
import kr.co.lunatalk.global.exception.CustomException;
import kr.co.lunatalk.global.exception.ErrorCode;

import java.util.List;

public record FindProductDto(
		Long productId,
		String productName,
		Long price,
		Integer quantity,
		ProductVisibility visibility,
		List<String> colors,
		CategoryResponse category,
		List<Image> images) {

	public static FindProductDto from(Product product, List<Image> images) {

		    if (product == null) {
			       throw new CustomException(ErrorCode.PRODUCT_NOT_FOUND);
			    }
			   List<Image> safeImages = images != null ? images : List.of();

		return new FindProductDto(product.getId(),
			product.getName(),
			product.getPrice(),
			product.getQuantity(),
			product.getVisibility(),
			product.getProductColor() != null
				? product.getProductColor().stream().map(ProductColor::getColor).toList()
				: List.of(),
			product.getCategory() != null ? CategoryResponse.from(product.getCategory()) : null,
			safeImages);
	}
}
