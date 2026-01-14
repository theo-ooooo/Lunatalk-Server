package kr.co.lunatalk.domain.productlike.service;

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
import kr.co.lunatalk.domain.product.repository.ProductRepository;
import kr.co.lunatalk.domain.productlike.domain.ProductLike;
import kr.co.lunatalk.domain.productlike.repository.ProductLikeRepository;
import kr.co.lunatalk.global.security.PrincipalDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class ProductLikeServiceTest {

	@Autowired
	ProductLikeService productLikeService;

	@Autowired
	ProductLikeRepository productLikeRepository;

	@Autowired
	MemberRepository memberRepository;

	@Autowired
	ProductRepository productRepository;

	@PersistenceContext
	EntityManager em;

	private Member member1;
	private Member member2;
	private Product product1;
	private Product product2;

	@BeforeEach
	void setup() {
		member1 = Member.createMember(
			"testuser1",
			"1234",
			Profile.of("테스트유저1", "img1"),
			"01012341234",
			"test1@test.com"
		);
		member2 = Member.createMember(
			"testuser2",
			"1234",
			Profile.of("테스트유저2", "img2"),
			"01012345678",
			"test2@test.com"
		);
		memberRepository.save(member1);
		memberRepository.save(member2);

		product1 = Product.createProduct("상품1", 10000L, 10, ProductStatus.ACTIVE, ProductVisibility.VISIBLE);
		ProductColor color1 = ProductColor.createProductColor(product1, "red");
		product1.addProductColor(color1);
		productRepository.save(product1);

		product2 = Product.createProduct("상품2", 20000L, 20, ProductStatus.ACTIVE, ProductVisibility.VISIBLE);
		ProductColor color2 = ProductColor.createProductColor(product2, "blue");
		product2.addProductColor(color2);
		productRepository.save(product2);

		// member1으로 인증 설정
		PrincipalDetails principalDetails = new PrincipalDetails(member1.getId(), MemberRole.USER);
		SecurityContextHolder.getContext().setAuthentication(
			new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities())
		);
	}

	@Test
	@DisplayName("좋아요를 누르면 ProductLike가 생성된다")
	void 좋아요_추가_테스트() {
		// given
		Long productId = product1.getId();

		// when
		productLikeService.toggleLike(productId);

		// then
		Optional<ProductLike> productLike = productLikeRepository.findByMemberIdAndProductId(
			member1.getId(), productId
		);
		assertTrue(productLike.isPresent());
		assertEquals(member1.getId(), productLike.get().getMember().getId());
		assertEquals(productId, productLike.get().getProduct().getId());
	}

	@Test
	@DisplayName("이미 좋아요를 누른 상태에서 다시 누르면 좋아요가 취소된다")
	void 좋아요_취소_테스트() {
		// given
		Long productId = product1.getId();
		productLikeService.toggleLike(productId); // 좋아요 추가

		// when
		productLikeService.toggleLike(productId); // 좋아요 취소

		// then
		Optional<ProductLike> productLike = productLikeRepository.findByMemberIdAndProductId(
			member1.getId(), productId
		);
		assertFalse(productLike.isPresent());
	}

	@Test
	@DisplayName("상품의 좋아요 개수를 조회할 수 있다")
	void 좋아요_개수_조회_테스트() {
		// given
		Long productId = product1.getId();
		productLikeService.toggleLike(productId); // member1이 좋아요

		// member2로 인증 변경
		PrincipalDetails principalDetails2 = new PrincipalDetails(member2.getId(), MemberRole.USER);
		SecurityContextHolder.getContext().setAuthentication(
			new UsernamePasswordAuthenticationToken(principalDetails2, null, principalDetails2.getAuthorities())
		);
		productLikeService.toggleLike(productId); // member2가 좋아요

		// when
		Long likeCount = productLikeService.getLikeCount(productId);

		// then
		assertEquals(2L, likeCount);
	}

	@Test
	@DisplayName("여러 상품의 좋아요 개수를 일괄 조회할 수 있다")
	void 여러_상품_좋아요_개수_조회_테스트() {
		// given
		productLikeService.toggleLike(product1.getId()); // member1이 product1 좋아요

		// member2로 인증 변경
		PrincipalDetails principalDetails2 = new PrincipalDetails(member2.getId(), MemberRole.USER);
		SecurityContextHolder.getContext().setAuthentication(
			new UsernamePasswordAuthenticationToken(principalDetails2, null, principalDetails2.getAuthorities())
		);
		productLikeService.toggleLike(product1.getId()); // member2가 product1 좋아요
		productLikeService.toggleLike(product2.getId()); // member2가 product2 좋아요

		// when
		Map<Long, Long> likeCounts = productLikeService.getLikeCounts(
			List.of(product1.getId(), product2.getId())
		);

		// then
		assertEquals(2, likeCounts.size());
		assertEquals(2L, likeCounts.get(product1.getId()));
		assertEquals(1L, likeCounts.get(product2.getId()));
	}

	@Test
	@DisplayName("특정 회원이 상품에 좋아요를 눌렀는지 확인할 수 있다")
	void 좋아요_여부_확인_테스트() {
		// given
		Long productId = product1.getId();
		productLikeService.toggleLike(productId); // member1이 좋아요

		// when
		Boolean isLikedByMember1 = productLikeService.isLiked(productId, member1.getId());
		Boolean isLikedByMember2 = productLikeService.isLiked(productId, member2.getId());

		// then
		assertTrue(isLikedByMember1);
		assertFalse(isLikedByMember2);
	}

	@Test
	@DisplayName("여러 상품에 대한 좋아요 여부를 일괄 확인할 수 있다")
	void 여러_상품_좋아요_여부_확인_테스트() {
		// given
		productLikeService.toggleLike(product1.getId()); // member1이 product1 좋아요
		// product2는 좋아요 안 함

		// when
		Map<Long, Boolean> likedStatus = productLikeService.getLikedStatus(
			List.of(product1.getId(), product2.getId()),
			member1.getId()
		);

		// then
		assertEquals(2, likedStatus.size());
		assertTrue(likedStatus.get(product1.getId()));
		assertFalse(likedStatus.get(product2.getId()));
	}

	@Test
	@DisplayName("비회원의 경우 좋아요 여부는 false를 반환한다")
	void 비회원_좋아요_여부_테스트() {
		// given
		Long productId = product1.getId();
		productLikeService.toggleLike(productId); // member1이 좋아요

		// when
		Boolean isLiked = productLikeService.isLiked(productId, null);

		// then
		assertFalse(isLiked);
	}

	@Test
	@DisplayName("좋아요가 없는 상품의 개수는 0을 반환한다")
	void 좋아요_없는_상품_개수_테스트() {
		// given
		Long productId = product1.getId();

		// when
		Long likeCount = productLikeService.getLikeCount(productId);

		// then
		assertEquals(0L, likeCount);
	}
}

