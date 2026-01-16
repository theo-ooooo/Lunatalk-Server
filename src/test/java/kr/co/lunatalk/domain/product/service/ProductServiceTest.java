package kr.co.lunatalk.domain.product.service;

import kr.co.lunatalk.domain.category.domain.Category;
import kr.co.lunatalk.domain.category.domain.CategoryStatus;
import kr.co.lunatalk.domain.category.domain.CategoryVisibility;
import kr.co.lunatalk.domain.image.domain.Image;
import kr.co.lunatalk.domain.product.domain.Product;
import kr.co.lunatalk.domain.product.domain.ProductColor;
import kr.co.lunatalk.domain.product.domain.ProductStatus;
import kr.co.lunatalk.domain.product.domain.ProductVisibility;
import kr.co.lunatalk.domain.product.dto.ProductWithImagesResult;
import kr.co.lunatalk.domain.product.dto.request.ProductCreateRequest;
import kr.co.lunatalk.domain.product.dto.request.ProductUpdateRequest;
import kr.co.lunatalk.domain.product.dto.response.ProductFindResponse;
import kr.co.lunatalk.domain.category.repository.CategoryRepository;
import kr.co.lunatalk.domain.image.repository.ImageRepository;
import kr.co.lunatalk.domain.product.repository.ProductRepository;
import kr.co.lunatalk.domain.productlike.service.ProductLikeService;
import kr.co.lunatalk.global.exception.CustomException;
import kr.co.lunatalk.global.util.ProductUtil;
import kr.co.lunatalk.global.util.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

	@InjectMocks
	private ProductService productService;

	@Mock
	private ProductRepository productRepository;

	@Mock
	private ImageRepository imageRepository;

	@Mock
	private CategoryRepository categoryRepository;

	@Mock
	private ProductUtil productUtil;

	@Mock
	private ProductLikeService productLikeService;

	@Mock
	private SecurityUtil securityUtil;

	private Product product1;
	private Product product2;
	private Category category;

	@BeforeEach
	void setUp() {
		product1 = Product.createProduct("상품1", 10000L, 10, ProductStatus.ACTIVE, ProductVisibility.VISIBLE);
		ProductColor color1 = ProductColor.createProductColor(product1, "red");
		product1.addProductColor(color1);
		ReflectionTestUtils.setField(product1, "id", 1L);

		product2 = Product.createProduct("상품2", 20000L, 20, ProductStatus.ACTIVE, ProductVisibility.VISIBLE);
		ProductColor color2 = ProductColor.createProductColor(product2, "blue");
		product2.addProductColor(color2);
		ReflectionTestUtils.setField(product2, "id", 2L);

		category = Category.createCategory("테스트카테고리", CategoryVisibility.VISIBLE);
		ReflectionTestUtils.setField(category, "id", 100L);
	}

	@Test
	@DisplayName("상품 상세 조회 시 좋아요 정보가 포함된다")
	void 상품_상세_조회_좋아요_정보_포함_테스트() {
		// given
		Long productId = product1.getId();
		List<Image> images = List.of();
		Long likeCount = 1L;
		Boolean isLiked = true;

		when(productUtil.findProductId(productId)).thenReturn(product1);
		when(imageRepository.fetchProductImagesByProductId(productId)).thenReturn(images);
		when(productLikeService.getLikeCount(productId)).thenReturn(likeCount);
		when(securityUtil.getCurrentMemberId()).thenReturn(1L);
		when(productLikeService.isLiked(productId, 1L)).thenReturn(isLiked);

		// when
		ProductFindResponse response = productService.findProductOne(productId);

		// then
		assertThat(response).isNotNull();
		assertThat(response.productId()).isEqualTo(productId);
		assertThat(response.likeCount()).isEqualTo(likeCount);
		assertThat(response.isLiked()).isTrue();
	}

	@Test
	@DisplayName("좋아요를 누르지 않은 상품은 isLiked가 false이다")
	void 좋아요_없는_상품_조회_테스트() {
		// given
		Long productId = product1.getId();
		List<Image> images = List.of();
		Long likeCount = 0L;
		Boolean isLiked = false;

		when(productUtil.findProductId(productId)).thenReturn(product1);
		when(imageRepository.fetchProductImagesByProductId(productId)).thenReturn(images);
		when(productLikeService.getLikeCount(productId)).thenReturn(likeCount);
		when(securityUtil.getCurrentMemberId()).thenReturn(1L);
		when(productLikeService.isLiked(productId, 1L)).thenReturn(isLiked);

		// when
		ProductFindResponse response = productService.findProductOne(productId);

		// then
		assertThat(response).isNotNull();
		assertThat(response.likeCount()).isEqualTo(0L);
		assertThat(response.isLiked()).isFalse();
	}

	@Test
	@DisplayName("상품 목록 조회 시 각 상품의 좋아요 정보가 포함된다")
	void 상품_목록_조회_좋아요_정보_포함_테스트() {
		// given
		List<Product> products = List.of(product1, product2);
		Page<Product> productPage = new PageImpl<>(products, PageRequest.of(0, 10), 2);
		List<Image> images = List.of();
		Map<Long, List<Image>> imageMap = Map.of(product1.getId(), images, product2.getId(), images);
		Map<Long, Long> likeCountMap = Map.of(product1.getId(), 1L, product2.getId(), 0L);
		Map<Long, Boolean> likedStatusMap = Map.of(product1.getId(), true, product2.getId(), false);

		when(productRepository.findAll(isNull(String.class), any(org.springframework.data.domain.Pageable.class))).thenReturn(productPage);
		when(imageRepository.fetchProductImagesByProductIds(anyList())).thenReturn(images);
		when(productLikeService.getLikeCounts(anyList())).thenReturn(likeCountMap);
		when(securityUtil.getCurrentMemberId()).thenReturn(1L);
		when(productLikeService.getLikedStatus(anyList(), eq(1L))).thenReturn(likedStatusMap);

		// when
		Page<ProductFindResponse> result = productService.findAll(null, PageRequest.of(0, 10));

		// then
		assertThat(result).isNotNull();
		assertThat(result.getTotalElements()).isEqualTo(2);

		ProductFindResponse product1Response = result.getContent().stream()
			.filter(p -> p.productId().equals(product1.getId()))
			.findFirst()
			.orElse(null);

		ProductFindResponse product2Response = result.getContent().stream()
			.filter(p -> p.productId().equals(product2.getId()))
			.findFirst()
			.orElse(null);

		assertThat(product1Response).isNotNull();
		assertThat(product1Response.likeCount()).isEqualTo(1L);
		assertThat(product1Response.isLiked()).isTrue();

		assertThat(product2Response).isNotNull();
		assertThat(product2Response.likeCount()).isEqualTo(0L);
		assertThat(product2Response.isLiked()).isFalse();
	}

	@Test
	@DisplayName("상품을 생성할 수 있다")
	void 상품_생성_테스트() {
		// given
		ProductCreateRequest request = new ProductCreateRequest(
			"새 상품",
			15000L,
			30,
			ProductVisibility.VISIBLE,
			List.of("red", "blue"),
			category.getId()
		);

		when(categoryRepository.findByIdAndStatus(category.getId(), CategoryStatus.ACTIVE))
			.thenReturn(Optional.of(category));
		when(productRepository.save(any(Product.class)))
			.thenAnswer(invocation -> {
				Product saved = invocation.getArgument(0);
				ReflectionTestUtils.setField(saved, "id", 3L);
				return saved;
			});

		// when
		Product savedProduct = productService.save(request);

		// then
		assertThat(savedProduct.getId()).isNotNull();
		assertThat(savedProduct.getName()).isEqualTo("새 상품");
		assertThat(savedProduct.getPrice()).isEqualTo(15000L);
		assertThat(savedProduct.getQuantity()).isEqualTo(30);
		assertThat(savedProduct.getVisibility()).isEqualTo(ProductVisibility.VISIBLE);
		assertThat(savedProduct.getProductColor()).hasSize(2);
		assertThat(savedProduct.getCategory().getId()).isEqualTo(category.getId());
	}

	@Test
	@DisplayName("존재하지 않는 카테고리로 상품 생성 시 예외가 발생한다")
	void 존재하지_않는_카테고리로_상품_생성_실패_테스트() {
		// given
		ProductCreateRequest request = new ProductCreateRequest(
			"새 상품",
			15000L,
			30,
			ProductVisibility.VISIBLE,
			List.of("red"),
			-1L
		);

		when(categoryRepository.findByIdAndStatus(-1L, CategoryStatus.ACTIVE))
			.thenReturn(Optional.empty());

		// when & then
		assertThrows(CustomException.class, () -> productService.save(request));
	}

	@Test
	@DisplayName("상품 정보를 수정할 수 있다")
	void 상품_수정_테스트() {
		// given
		ProductUpdateRequest request = new ProductUpdateRequest(
			"수정된 상품명",
			20000L,
			50,
			ProductVisibility.HIDDEN,
			List.of("green", "yellow"),
			category.getId()
		);

		when(productUtil.findProductId(product1.getId())).thenReturn(product1);
		when(categoryRepository.findByIdAndStatus(category.getId(), CategoryStatus.ACTIVE))
			.thenReturn(Optional.of(category));

		// when
		productService.update(product1.getId(), request);

		// then
		assertThat(product1.getName()).isEqualTo("수정된 상품명");
		assertThat(product1.getPrice()).isEqualTo(20000L);
		assertThat(product1.getQuantity()).isEqualTo(50);
		assertThat(product1.getVisibility()).isEqualTo(ProductVisibility.HIDDEN);
	}

	@Test
	@DisplayName("상품을 삭제하면 상태가 DELETED로 변경되고 visibility가 HIDDEN으로 변경된다")
	void 상품_삭제_테스트() {
		// given
		Long productId = product1.getId();

		when(productUtil.findProductId(productId)).thenReturn(product1);

		// when
		productService.delete(productId);

		// then
		assertThat(product1.getStatus()).isEqualTo(ProductStatus.DELETED);
		assertThat(product1.getVisibility()).isEqualTo(ProductVisibility.HIDDEN);
	}

	@Test
	@DisplayName("존재하지 않는 상품 조회 시 예외가 발생한다")
	void 존재하지_않는_상품_조회_실패_테스트() {
		// given
		Long nonExistentProductId = -1L;

		when(productUtil.findProductId(nonExistentProductId))
			.thenThrow(new CustomException(kr.co.lunatalk.global.exception.ErrorCode.PRODUCT_NOT_FOUND));

		// when & then
		assertThrows(CustomException.class, () -> productService.findProductOne(nonExistentProductId));
	}

	@Test
	@DisplayName("여러 상품을 ID 리스트로 조회할 수 있다")
	void 여러_상품_조회_테스트() {
		// given
		List<Long> productIds = List.of(product1.getId(), product2.getId());
		List<Product> products = List.of(product1, product2);
		Map<Long, List<Image>> imageMap = Map.of(product1.getId(), List.of(), product2.getId(), List.of());
		Map<Long, Long> likeCountMap = Map.of(product1.getId(), 1L, product2.getId(), 0L);
		Map<Long, Boolean> likedStatusMap = Map.of(product1.getId(), true, product2.getId(), false);

		when(productUtil.findAllProducts(productIds))
			.thenReturn(new ProductWithImagesResult(products, imageMap));
		when(productLikeService.getLikeCounts(productIds)).thenReturn(likeCountMap);
		when(securityUtil.getCurrentMemberId()).thenReturn(1L);
		when(productLikeService.getLikedStatus(productIds, 1L)).thenReturn(likedStatusMap);

		// when
		List<ProductFindResponse> responses = productService.findAllProducts(productIds);

		// then
		assertThat(responses).hasSize(2);
		ProductFindResponse product1Response = responses.stream()
			.filter(p -> p.productId().equals(product1.getId()))
			.findFirst()
			.orElseThrow();
		ProductFindResponse product2Response = responses.stream()
			.filter(p -> p.productId().equals(product2.getId()))
			.findFirst()
			.orElseThrow();

		assertThat(product1Response.likeCount()).isEqualTo(1L);
		assertThat(product1Response.isLiked()).isTrue();
		assertThat(product2Response.likeCount()).isEqualTo(0L);
		assertThat(product2Response.isLiked()).isFalse();
	}

	@Test
	@DisplayName("상품명으로 검색할 수 있다")
	void 상품명_검색_테스트() {
		// given
		Product product3 = Product.createProduct("검색테스트상품", 30000L, 30, ProductStatus.ACTIVE, ProductVisibility.VISIBLE);
		ProductColor color3 = ProductColor.createProductColor(product3, "green");
		product3.addProductColor(color3);
		ReflectionTestUtils.setField(product3, "id", 3L);

		List<Product> products = List.of(product3);
		Page<Product> productPage = new PageImpl<>(products, PageRequest.of(0, 10), 1);
		List<Image> images = List.of();
		Map<Long, Long> likeCountMap = Map.of(product3.getId(), 0L);
		Map<Long, Boolean> likedStatusMap = Map.of(product3.getId(), false);

		when(productRepository.findAll(eq("검색테스트"), any(org.springframework.data.domain.Pageable.class))).thenReturn(productPage);
		when(imageRepository.fetchProductImagesByProductIds(anyList())).thenReturn(images);
		when(productLikeService.getLikeCounts(anyList())).thenReturn(likeCountMap);
		when(securityUtil.getCurrentMemberId()).thenReturn(1L);
		when(productLikeService.getLikedStatus(anyList(), eq(1L))).thenReturn(likedStatusMap);

		// when
		Page<ProductFindResponse> result = productService.findAll("검색테스트", PageRequest.of(0, 10));

		// then
		assertThat(result).isNotNull();
		assertThat(result.getTotalElements()).isEqualTo(1);
		assertThat(result.getContent().stream()
			.anyMatch(p -> p.name().contains("검색테스트"))).isTrue();
	}

	@Test
	@DisplayName("상품명이 null이면 전체 상품을 조회한다")
	void 전체_상품_조회_테스트() {
		// given
		List<Product> products = List.of(product1, product2);
		Page<Product> productPage = new PageImpl<>(products, PageRequest.of(0, 10), 2);
		List<Image> images = List.of();
		Map<Long, Long> likeCountMap = Map.of(product1.getId(), 0L, product2.getId(), 0L);
		Map<Long, Boolean> likedStatusMap = Map.of(product1.getId(), false, product2.getId(), false);

		when(productRepository.findAll(isNull(String.class), any(org.springframework.data.domain.Pageable.class))).thenReturn(productPage);
		when(imageRepository.fetchProductImagesByProductIds(anyList())).thenReturn(images);
		when(productLikeService.getLikeCounts(anyList())).thenReturn(likeCountMap);
		when(securityUtil.getCurrentMemberId()).thenReturn(1L);
		when(productLikeService.getLikedStatus(anyList(), eq(1L))).thenReturn(likedStatusMap);

		// when
		Page<ProductFindResponse> result = productService.findAll(null, PageRequest.of(0, 10));

		// then
		assertThat(result).isNotNull();
		assertThat(result.getTotalElements()).isEqualTo(2);
	}
}
