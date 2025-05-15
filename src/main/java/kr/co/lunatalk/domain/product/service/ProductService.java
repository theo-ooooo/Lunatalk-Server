package kr.co.lunatalk.domain.product.service;

import kr.co.lunatalk.domain.category.domain.CategoryStatus;
import kr.co.lunatalk.domain.category.repository.CategoryRepository;
import kr.co.lunatalk.domain.image.domain.Image;
import kr.co.lunatalk.domain.image.repository.ImageRepository;
import kr.co.lunatalk.domain.product.domain.Product;
import kr.co.lunatalk.domain.product.domain.ProductColor;
import kr.co.lunatalk.domain.product.domain.ProductStatus;
import kr.co.lunatalk.domain.product.dto.FindProductDto;
import kr.co.lunatalk.domain.product.dto.ProductWithImagesResult;
import kr.co.lunatalk.domain.product.dto.request.ProductCreateRequest;
import kr.co.lunatalk.domain.product.dto.request.ProductUpdateRequest;
import kr.co.lunatalk.domain.product.dto.response.ProductFindResponse;
import kr.co.lunatalk.domain.product.repository.ProductRepository;
import kr.co.lunatalk.global.exception.CustomException;
import kr.co.lunatalk.global.exception.ErrorCode;
import kr.co.lunatalk.global.util.ProductUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
	private final CategoryRepository categoryRepository;
	private final ProductUtil productUtil;

	public Product save(ProductCreateRequest request) {
		// 상품 저장.
		Product product = Product.createProduct(request.name(), request.price(), request.quantity(), ProductStatus.ACTIVE, request.visibility());
		// 색상 저장.
		request.colors().forEach(color -> {
			ProductColor productColor = ProductColor.createProductColor(product, color);
			product.addProductColor(productColor);
		});

		updateCategory(request.categoryId(), product);

		productRepository.save(product);
		return product;
	}

	public void update(Long productId, ProductUpdateRequest request) {
		Product findProduct = findById(productId);
		findProduct.updateProduct(request);

		updateCategory(request.categoryId(), findProduct);
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
		ProductWithImagesResult allProducts = productUtil.findAllProducts(productIds);

		return allProducts.products().stream().map(product -> {
			return ProductFindResponse.from(FindProductDto.from(product, allProducts.imageMap().getOrDefault(product.getId(), List.of())));
		}).toList();
	}

	private void updateCategory(Long categoryId, Product product) {
		categoryRepository.findByIdAndStatus(categoryId, CategoryStatus.ACTIVE).ifPresentOrElse(
			product::setCategory,
			() -> {throw new CustomException(ErrorCode.CATEGORY_NOT_FOUND);}
		);
	}

	@Transactional(readOnly = true)
	public Page<ProductFindResponse> findAll(String productName, Pageable pageable) {
		Page<Product> products = productRepository.findAll(productName, pageable);


		List<Long> productIds = products.stream().map(Product::getId).toList();

		List<Image> images = imageRepository.fetchProductImagesByProductIds(productIds);

		Map<Long, List<Image>> imageMap = images.stream().collect(Collectors.groupingBy(Image::getReferenceId));

		return products
			.map(product -> {
				List<Image> productImages = imageMap.getOrDefault(product.getId(), List.of());
				return ProductFindResponse.from(FindProductDto.from(product, productImages));
			});
	}
}
