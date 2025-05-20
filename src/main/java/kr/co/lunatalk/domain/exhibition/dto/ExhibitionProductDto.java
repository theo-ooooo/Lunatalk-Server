package kr.co.lunatalk.domain.exhibition.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.lunatalk.domain.image.domain.Image;
import kr.co.lunatalk.domain.product.domain.Product;
import kr.co.lunatalk.domain.product.dto.FindProductDto;

import java.util.List;

public record ExhibitionProductDto(
	@Schema(description = "상품 정보")
	FindProductDto product,

	@Schema(description = "기획전 내 정렬 순서")
	int sortOrder
) {
	public static ExhibitionProductDto from(Product product, List<Image> images, int sortOrder) {
		return new ExhibitionProductDto(FindProductDto.from(product, images), sortOrder);
	}
}

