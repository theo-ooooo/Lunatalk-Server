package kr.co.lunatalk.domain.product.dto.response;

import kr.co.lunatalk.domain.image.dto.FindImageDto;
import kr.co.lunatalk.domain.product.domain.ProductColor;
import kr.co.lunatalk.domain.product.dto.FindProductDto;

import java.util.List;

public record ProductFindResponse(
	Long productId,
	String name,
	Long price,
	List<String> colors,
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
