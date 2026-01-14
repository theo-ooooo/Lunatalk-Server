package kr.co.lunatalk.domain.product.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import kr.co.lunatalk.domain.member.domain.Member;
import kr.co.lunatalk.domain.member.domain.MemberRole;
import kr.co.lunatalk.domain.member.domain.Profile;
import kr.co.lunatalk.domain.member.repository.MemberRepository;
import kr.co.lunatalk.domain.product.domain.Product;
import kr.co.lunatalk.domain.product.domain.ProductColor;
import kr.co.lunatalk.domain.product.domain.ProductStatus;
import kr.co.lunatalk.domain.product.domain.ProductVisibility;
import kr.co.lunatalk.domain.category.domain.Category;
import kr.co.lunatalk.domain.category.domain.CategoryStatus;
import kr.co.lunatalk.domain.category.domain.CategoryVisibility;
import kr.co.lunatalk.domain.category.repository.CategoryRepository;
import kr.co.lunatalk.domain.product.dto.request.ProductCreateRequest;
import kr.co.lunatalk.domain.product.dto.request.ProductUpdateRequest;
import kr.co.lunatalk.domain.product.dto.response.ProductFindResponse;
import kr.co.lunatalk.domain.product.repository.ProductRepository;
import kr.co.lunatalk.domain.productlike.repository.ProductLikeRepository;
import kr.co.lunatalk.global.exception.CustomException;
import kr.co.lunatalk.global.exception.ErrorCode;
import kr.co.lunatalk.global.security.PrincipalDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class ProductServiceTest {

	@Autowired
	ProductService productService;

	@Autowired
	ProductRepository productRepository;

	@Autowired
	MemberRepository memberRepository;

	@Autowired
	ProductLikeRepository productLikeRepository;

	@Autowired
	CategoryRepository categoryRepository;

	@PersistenceContext
	EntityManager em;

	private Member member;
	private Product product1;
	private Product product2;
	private Category category;

	@BeforeEach
	void setup() {
		member = Member.createMember(
			"testuser",
			"1234",
			Profile.of("테스트유저", "img"),
			"01012341234",
			"test@test.com"
		);
		memberRepository.save(member);

		category = Category.createCategory("테스트카테고리", CategoryVisibility.VISIBLE);
		categoryRepository.save(category);

		product1 = Product.createProduct("상품1", 10000L, 10, ProductStatus.ACTIVE, ProductVisibility.VISIBLE);
		ProductColor color1 = ProductColor.createProductColor(product1, "red");
		product1.addProductColor(color1);
		productRepository.save(product1);

		product2 = Product.createProduct("상품2", 20000L, 20, ProductStatus.ACTIVE, ProductVisibility.VISIBLE);
		ProductColor color2 = ProductColor.createProductColor(product2, "blue");
		product2.addProductColor(color2);
		productRepository.save(product2);

		// member로 인증 설정
		PrincipalDetails principalDetails = new PrincipalDetails(member.getId(), MemberRole.USER);
		SecurityContextHolder.getContext().setAuthentication(
			new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities())
		);
	}

	@Test
	@DisplayName("상품 상세 조회 시 좋아요 정보가 포함된다")
	void 상품_상세_조회_좋아요_정보_포함_테스트() {
		// given
		Long productId = product1.getId();
		// 좋아요 추가
		productLikeRepository.save(
			kr.co.lunatalk.domain.productlike.domain.ProductLike.create(member, product1)
		);

		// when
		ProductFindResponse response = productService.findProductOne(productId);

		// then
		assertNotNull(response);
		assertEquals(productId, response.productId());
		assertEquals(1L, response.likeCount());
		assertTrue(response.isLiked());
	}

	@Test
	@DisplayName("좋아요를 누르지 않은 상품은 isLiked가 false이다")
	void 좋아요_없는_상품_조회_테스트() {
		// given
		Long productId = product1.getId();

		// when
		ProductFindResponse response = productService.findProductOne(productId);

		// then
		assertNotNull(response);
		assertEquals(0L, response.likeCount());
		assertFalse(response.isLiked());
	}

	@Test
	@DisplayName("상품 목록 조회 시 각 상품의 좋아요 정보가 포함된다")
	void 상품_목록_조회_좋아요_정보_포함_테스트() {
		// given
		// product1에만 좋아요 추가
		productLikeRepository.save(
			kr.co.lunatalk.domain.productlike.domain.ProductLike.create(member, product1)
		);

		// when
		Page<ProductFindResponse> result = productService.findAll(null, PageRequest.of(0, 10));

		// then
		assertNotNull(result);
		assertTrue(result.getTotalElements() >= 2);

		ProductFindResponse product1Response = result.getContent().stream()
			.filter(p -> p.productId().equals(product1.getId()))
			.findFirst()
			.orElse(null);

		ProductFindResponse product2Response = result.getContent().stream()
			.filter(p -> p.productId().equals(product2.getId()))
			.findFirst()
			.orElse(null);

		assertNotNull(product1Response);
		assertEquals(1L, product1Response.likeCount());
		assertTrue(product1Response.isLiked());

		assertNotNull(product2Response);
		assertEquals(0L, product2Response.likeCount());
		assertFalse(product2Response.isLiked());
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

		// when
		Product savedProduct = productService.save(request);

		// then
		assertNotNull(savedProduct.getId());
		assertEquals("새 상품", savedProduct.getName());
		assertEquals(15000L, savedProduct.getPrice());
		assertEquals(30, savedProduct.getQuantity());
		assertEquals(ProductVisibility.VISIBLE, savedProduct.getVisibility());
		assertEquals(2, savedProduct.getProductColor().size());
		assertEquals(category.getId(), savedProduct.getCategory().getId());
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
			-1L // 존재하지 않는 카테고리 ID
		);

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

		// when
		productService.update(product1.getId(), request);

		// then
		em.flush();
		em.clear();
		Product updatedProduct = productRepository.findById(product1.getId()).orElseThrow();
		assertEquals("수정된 상품명", updatedProduct.getName());
		assertEquals(20000L, updatedProduct.getPrice());
		assertEquals(50, updatedProduct.getQuantity());
		assertEquals(ProductVisibility.HIDDEN, updatedProduct.getVisibility());
		assertEquals(2, updatedProduct.getProductColor().size());
		assertTrue(updatedProduct.getProductColor().stream()
			.anyMatch(pc -> pc.getColor().equals("green")));
		assertTrue(updatedProduct.getProductColor().stream()
			.anyMatch(pc -> pc.getColor().equals("yellow")));
	}

	@Test
	@DisplayName("상품을 삭제하면 상태가 DELETED로 변경되고 visibility가 HIDDEN으로 변경된다")
	void 상품_삭제_테스트() {
		// given
		Long productId = product1.getId();

		// when
		productService.delete(productId);

		// then
		em.flush();
		em.clear();
		Product deletedProduct = productRepository.findById(productId).orElseThrow();
		assertEquals(ProductStatus.DELETED, deletedProduct.getStatus());
		assertEquals(ProductVisibility.HIDDEN, deletedProduct.getVisibility());
	}

	@Test
	@DisplayName("존재하지 않는 상품 조회 시 예외가 발생한다")
	void 존재하지_않는_상품_조회_실패_테스트() {
		// given
		Long nonExistentProductId = -1L;

		// when & then
		assertThrows(CustomException.class, () -> productService.findProductOne(nonExistentProductId));
	}

	@Test
	@DisplayName("여러 상품을 ID 리스트로 조회할 수 있다")
	void 여러_상품_조회_테스트() {
		// given
		List<Long> productIds = List.of(product1.getId(), product2.getId());
		// product1에 좋아요 추가
		productLikeRepository.save(
			kr.co.lunatalk.domain.productlike.domain.ProductLike.create(member, product1)
		);

		// when
		List<ProductFindResponse> responses = productService.findAllProducts(productIds);

		// then
		assertEquals(2, responses.size());
		ProductFindResponse product1Response = responses.stream()
			.filter(p -> p.productId().equals(product1.getId()))
			.findFirst()
			.orElseThrow();
		ProductFindResponse product2Response = responses.stream()
			.filter(p -> p.productId().equals(product2.getId()))
			.findFirst()
			.orElseThrow();

		assertEquals(1L, product1Response.likeCount());
		assertTrue(product1Response.isLiked());
		assertEquals(0L, product2Response.likeCount());
		assertFalse(product2Response.isLiked());
	}

	@Test
	@DisplayName("상품명으로 검색할 수 있다")
	void 상품명_검색_테스트() {
		// given
		Product product3 = Product.createProduct("검색테스트상품", 30000L, 30, ProductStatus.ACTIVE, ProductVisibility.VISIBLE);
		ProductColor color3 = ProductColor.createProductColor(product3, "green");
		product3.addProductColor(color3);
		productRepository.save(product3);

		// when
		Page<ProductFindResponse> result = productService.findAll("검색테스트", PageRequest.of(0, 10));

		// then
		assertNotNull(result);
		assertTrue(result.getTotalElements() >= 1);
		assertTrue(result.getContent().stream()
			.anyMatch(p -> p.name().contains("검색테스트")));
	}

	@Test
	@DisplayName("상품명이 null이면 전체 상품을 조회한다")
	void 전체_상품_조회_테스트() {
		// when
		Page<ProductFindResponse> result = productService.findAll(null, PageRequest.of(0, 10));

		// then
		assertNotNull(result);
		assertTrue(result.getTotalElements() >= 2);
	}
}

