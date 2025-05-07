package kr.co.lunatalk.domain.product.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.lunatalk.domain.image.dto.FindImageDto;
import kr.co.lunatalk.domain.product.domain.ProductColor;
import kr.co.lunatalk.domain.product.dto.FindProductDto;

import java.util.List;

public record ProductFindResponse(
	@Schema(description = "상품 ID")
	Long productId,
	@Schema(description = "상품 이름")
	String name,
	@Schema(description = "상품 가격")
	Long price,
	@Schema(description = "상품 색상들")
	List<String> colors,
	@Schema(description = "상품 이미지")
	List<FindImageDto> images
) {

	public static ProductFindResponse from(FindProductDto findProductDto) {
		List<String> colors = findProductDto.product().getProductColor().stream()
			.map(ProductColor::getColor)
			.toList();

		List<FindImageDto> images = findProductDto.images().stream()
			.map(FindImageDto::from)
			.toList();

		return new ProductFindResponse(
			findProductDto.product().getId(),
			findProductDto.product().getName(),
			findProductDto.product().getPrice(),
			colors,
			images
		);
	}
}
