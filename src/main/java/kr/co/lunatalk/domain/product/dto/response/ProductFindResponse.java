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
		List<String> colors = findProductDto.colors().stream()
			.map(ProductColor::getColor)
			.toList();

		List<FindImageDto> images = findProductDto.images().stream()
			.map(FindImageDto::from)
			.toList();

		return new ProductFindResponse(
			findProductDto.productId(),
			findProductDto.name(),
			findProductDto.price(),
			colors,
			images
		);
	}
}
