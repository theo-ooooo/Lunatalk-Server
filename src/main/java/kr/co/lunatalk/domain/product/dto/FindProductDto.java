package kr.co.lunatalk.domain.product.dto;

import kr.co.lunatalk.domain.image.domain.Image;
import kr.co.lunatalk.domain.product.domain.Product;
import kr.co.lunatalk.domain.product.domain.ProductColor;

import java.util.List;

public record FindProductDto(Long productId, String name, Long price, List<ProductColor> colors, List<Image> images) {

	public static FindProductDto from(Product product, List<ProductColor> colors, List<Image> images) {
		return new FindProductDto(product.getId(), product.getName(), product.getPrice(), colors, images);
	}
}
