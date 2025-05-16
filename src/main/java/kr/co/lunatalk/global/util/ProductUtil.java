package kr.co.lunatalk.global.util;

import kr.co.lunatalk.domain.image.domain.Image;
import kr.co.lunatalk.domain.image.repository.ImageRepository;
import kr.co.lunatalk.domain.product.domain.Product;
import kr.co.lunatalk.domain.product.dto.FindProductDto;
import kr.co.lunatalk.domain.product.dto.ProductWithImagesResult;
import kr.co.lunatalk.domain.product.dto.response.ProductFindResponse;
import kr.co.lunatalk.domain.product.repository.ProductRepository;
import kr.co.lunatalk.global.exception.CustomException;
import kr.co.lunatalk.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductUtil {
	private final ProductRepository productRepository;
	private final ImageRepository imageRepository;

	@Transactional
	public Product findProductId(Long productId) {
		return productRepository.findProductById(productId).orElseThrow(
			() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND)
		);
	}

	@Transactional
	public List<Product> findAllProductByProductIdIn(List<Long> productIds) {
		return productRepository
			.findAllProductsByProductIds(productIds);
	}

	@Transactional(readOnly = true)
	public ProductWithImagesResult findAllProducts(List<Long> productIds) {
		List<Product> products = productRepository.findAllProductDtoByIdsWithJoin(productIds);

		if(products.isEmpty()) {
			return new ProductWithImagesResult(List.of(), Map.of());
		}

		List<Image> images = imageRepository.fetchProductImagesByProductIds(productIds);

		Map<Long, List<Image>> imageMap = images.stream().collect(Collectors.groupingBy(Image::getReferenceId));

		return new ProductWithImagesResult(products, imageMap);
	}
}
