package kr.co.lunatalk.domain.product.service;

import kr.co.lunatalk.domain.image.domain.Image;
import kr.co.lunatalk.domain.image.repository.ImageRepository;
import kr.co.lunatalk.domain.product.domain.Product;
import kr.co.lunatalk.domain.product.domain.ProductColor;
import kr.co.lunatalk.domain.product.dto.FindProductDto;
import kr.co.lunatalk.domain.product.dto.request.ProductCreateRequest;
import kr.co.lunatalk.domain.product.dto.request.ProductUpdateRequest;
import kr.co.lunatalk.domain.product.dto.response.ProductFindResponse;
import kr.co.lunatalk.domain.product.repository.ProductRepository;
import kr.co.lunatalk.global.exception.CustomException;
import kr.co.lunatalk.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {
	private final ProductRepository productRepository;
	private final ImageRepository imageRepository;

	public Product save(ProductCreateRequest request) {
		// 상품 저장.
		Product product = Product.createProduct(request.name(), request.price(), request.quantity(), request.status(), request.visibility());
		// 색상 저장.
		request.colors().forEach(color -> {
			ProductColor productColor = ProductColor.createProductColor(product, color);
			product.addProductColor(productColor);
		});

		productRepository.save(product);
		return product;
	}

	public void update(Long productId, ProductUpdateRequest request) {
		Product findProduct = findById(productId);
		findProduct.updateProduct(request);
	}

	public void delete(Long productId) {
		Product findProduct = findById(productId);

		findProduct.deleteProduct();
	}

	@Transactional(readOnly = true)
	public Product findById(Long id) {
		return productRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
	}

	@Transactional(readOnly = true)
	public ProductFindResponse findProductOne(Long productId) {
		Product findProduct = productRepository.findProductById(productId);

		if (findProduct == null) {
			throw new CustomException(ErrorCode.PRODUCT_NOT_FOUND);
		}

		List<Image> images = imageRepository.fetchProductImagesByProductId(findProduct.getId());


		return ProductFindResponse.from(FindProductDto.from(findProduct, images));
	}

	@Transactional(readOnly = true)
	public List<ProductFindResponse> findAllProducts(List<Long> productIds) {
		List<Product> products = productRepository.findAllProductDtoByIdsWithJoin(productIds);

		if(products.isEmpty()) {
			return List.of();
		}

		List<Image> images = imageRepository.fetchProductImagesByProductIds(productIds);

		Map<Long, List<Image>> imageMap = images.stream().collect(Collectors.groupingBy(Image::getReferenceId));

		return products.stream()
				.map(product -> {
					List<Image> productImages = imageMap.getOrDefault(product.getId(), List.of());
					return ProductFindResponse.from(FindProductDto.from(product, productImages));
				}).toList();
	}
}
