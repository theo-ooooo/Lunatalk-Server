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
import kr.co.lunatalk.domain.productlike.service.ProductLikeService;
import kr.co.lunatalk.global.exception.CustomException;
import kr.co.lunatalk.global.exception.ErrorCode;
import kr.co.lunatalk.global.util.ProductUtil;
import kr.co.lunatalk.global.util.SecurityUtil;
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
	private final ProductLikeService productLikeService;
	private final SecurityUtil securityUtil;

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
		Product findProduct = productUtil.findProductId(productId);
		findProduct.updateProduct(request);

		updateCategory(request.categoryId(), findProduct);
	}

	public void delete(Long productId) {
		Product findProduct = productUtil.findProductId(productId);

		findProduct.deleteProduct();
	}



	@Transactional(readOnly = true)
	public ProductFindResponse findProductOne(Long productId) {
		Product findProduct = productUtil.findProductId(productId);

		if (findProduct == null) {
			throw new CustomException(ErrorCode.PRODUCT_NOT_FOUND);
		}

		List<Image> images = imageRepository.fetchProductImagesByProductId(findProduct.getId());

		Long likeCount = productLikeService.getLikeCount(productId);
		Long currentMemberId = getCurrentMemberId();
		Boolean isLiked = productLikeService.isLiked(productId, currentMemberId);

		return ProductFindResponse.from(FindProductDto.from(findProduct, images, likeCount, isLiked));
	}

	@Transactional(readOnly = true)
	public List<ProductFindResponse> findAllProducts(List<Long> productIds) {
		ProductWithImagesResult allProducts = productUtil.findAllProducts(productIds);

		Map<Long, Long> likeCountMap = productLikeService.getLikeCounts(productIds);
		Long currentMemberId = getCurrentMemberId();
		Map<Long, Boolean> likedStatusMap = productLikeService.getLikedStatus(productIds, currentMemberId);

		return allProducts.products().stream().map(product -> {
			List<Image> productImages = allProducts.imageMap().getOrDefault(product.getId(), List.of());
			Long likeCount = likeCountMap.getOrDefault(product.getId(), 0L);
			Boolean isLiked = likedStatusMap.getOrDefault(product.getId(), false);
			return ProductFindResponse.from(FindProductDto.from(product, productImages, likeCount, isLiked));
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

		Map<Long, Long> likeCountMap = productLikeService.getLikeCounts(productIds);
		Long currentMemberId = getCurrentMemberId();
		Map<Long, Boolean> likedStatusMap = productLikeService.getLikedStatus(productIds, currentMemberId);

		return products
			.map(product -> {
				List<Image> productImages = imageMap.getOrDefault(product.getId(), List.of());
				Long likeCount = likeCountMap.getOrDefault(product.getId(), 0L);
				Boolean isLiked = likedStatusMap.getOrDefault(product.getId(), false);
				return ProductFindResponse.from(FindProductDto.from(product, productImages, likeCount, isLiked));
			});
	}

	private Long getCurrentMemberId() {
		try {
			return securityUtil.getCurrentMemberId();
		} catch (Exception e) {
			return null; // 비회원인 경우
		}
	}
}
