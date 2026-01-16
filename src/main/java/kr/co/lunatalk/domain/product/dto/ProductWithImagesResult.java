package kr.co.lunatalk.domain.product.dto;

import kr.co.lunatalk.domain.image.domain.Image;
import kr.co.lunatalk.domain.product.domain.Product;

import java.util.List;
import java.util.Map;

public record ProductWithImagesResult(
	List<Product> products,
	Map<Long, List<Image>> imageMap
) {
}
