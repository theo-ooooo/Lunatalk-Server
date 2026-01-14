package kr.co.lunatalk.domain.productlike.service;

import kr.co.lunatalk.domain.member.domain.Member;
import kr.co.lunatalk.domain.member.domain.MemberRole;
import kr.co.lunatalk.domain.member.domain.Profile;
import kr.co.lunatalk.domain.product.domain.Product;
import kr.co.lunatalk.domain.product.domain.ProductColor;
import kr.co.lunatalk.domain.product.domain.ProductStatus;
import kr.co.lunatalk.domain.product.domain.ProductVisibility;
import kr.co.lunatalk.domain.productlike.domain.ProductLike;
import kr.co.lunatalk.domain.productlike.repository.ProductLikeRepository;
import kr.co.lunatalk.global.util.MemberUtil;
import kr.co.lunatalk.global.util.ProductUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductLikeServiceTest {

	@InjectMocks
	private ProductLikeService productLikeService;

	@Mock
	private ProductLikeRepository productLikeRepository;

	@Mock
	private MemberUtil memberUtil;

	@Mock
	private ProductUtil productUtil;

	private Member member1;
	private Member member2;
	private Product product1;
	private Product product2;

	@BeforeEach
	void setUp() {
		member1 = Member.createMember(
			"testuser1",
			"1234",
			Profile.of("테스트유저1", "img1"),
			"01012341234",
			"test1@test.com"
		);
		ReflectionTestUtils.setField(member1, "id", 1L);

		member2 = Member.createMember(
			"testuser2",
			"1234",
			Profile.of("테스트유저2", "img2"),
			"01012345678",
			"test2@test.com"
		);
		ReflectionTestUtils.setField(member2, "id", 2L);

		product1 = Product.createProduct("상품1", 10000L, 10, ProductStatus.ACTIVE, ProductVisibility.VISIBLE);
		ProductColor color1 = ProductColor.createProductColor(product1, "red");
		product1.addProductColor(color1);
		ReflectionTestUtils.setField(product1, "id", 100L);

		product2 = Product.createProduct("상품2", 20000L, 20, ProductStatus.ACTIVE, ProductVisibility.VISIBLE);
		ProductColor color2 = ProductColor.createProductColor(product2, "blue");
		product2.addProductColor(color2);
		ReflectionTestUtils.setField(product2, "id", 200L);
	}

	@Test
	@DisplayName("좋아요를 누르면 ProductLike가 생성된다")
	void 좋아요_추가_테스트() {
		// given
		Long productId = product1.getId();

		when(memberUtil.getCurrentMember()).thenReturn(member1);
		when(productUtil.findProductId(productId)).thenReturn(product1);
		when(productLikeRepository.findByMemberIdAndProductId(member1.getId(), productId))
			.thenReturn(Optional.empty());
		when(productLikeRepository.save(any(ProductLike.class)))
			.thenAnswer(invocation -> {
				ProductLike saved = invocation.getArgument(0);
				ReflectionTestUtils.setField(saved, "id", 1L);
				return saved;
			});

		// when
		productLikeService.toggleLike(productId);

		// then
		verify(productLikeRepository).save(any(ProductLike.class));
		verify(productLikeRepository, never()).delete(any(ProductLike.class));
	}

	@Test
	@DisplayName("이미 좋아요를 누른 상태에서 다시 누르면 좋아요가 취소된다")
	void 좋아요_취소_테스트() {
		// given
		Long productId = product1.getId();
		ProductLike existingLike = ProductLike.create(member1, product1);
		ReflectionTestUtils.setField(existingLike, "id", 1L);

		when(memberUtil.getCurrentMember()).thenReturn(member1);
		when(productUtil.findProductId(productId)).thenReturn(product1);
		when(productLikeRepository.findByMemberIdAndProductId(member1.getId(), productId))
			.thenReturn(Optional.of(existingLike));

		// when
		productLikeService.toggleLike(productId);

		// then
		verify(productLikeRepository).delete(existingLike);
		verify(productLikeRepository, never()).save(any(ProductLike.class));
	}

	@Test
	@DisplayName("상품의 좋아요 개수를 조회할 수 있다")
	void 좋아요_개수_조회_테스트() {
		// given
		Long productId = product1.getId();
		when(productLikeRepository.countByProductId(productId)).thenReturn(2L);

		// when
		Long likeCount = productLikeService.getLikeCount(productId);

		// then
		assertThat(likeCount).isEqualTo(2L);
	}

	@Test
	@DisplayName("여러 상품의 좋아요 개수를 일괄 조회할 수 있다")
	void 여러_상품_좋아요_개수_조회_테스트() {
		// given
		List<Long> productIds = List.of(product1.getId(), product2.getId());
		Map<Long, Long> expectedCounts = Map.of(
			product1.getId(), 2L,
			product2.getId(), 1L
		);
		when(productLikeRepository.countByProductIds(productIds)).thenReturn(expectedCounts);

		// when
		Map<Long, Long> likeCounts = productLikeService.getLikeCounts(productIds);

		// then
		assertThat(likeCounts).hasSize(2);
		assertThat(likeCounts.get(product1.getId())).isEqualTo(2L);
		assertThat(likeCounts.get(product2.getId())).isEqualTo(1L);
	}

	@Test
	@DisplayName("특정 회원이 상품에 좋아요를 눌렀는지 확인할 수 있다")
	void 좋아요_여부_확인_테스트() {
		// given
		Long productId = product1.getId();
		ProductLike existingLike = ProductLike.create(member1, product1);
		ReflectionTestUtils.setField(existingLike, "id", 1L);

		when(productLikeRepository.findByMemberIdAndProductId(member1.getId(), productId))
			.thenReturn(Optional.of(existingLike));
		when(productLikeRepository.findByMemberIdAndProductId(member2.getId(), productId))
			.thenReturn(Optional.empty());

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
		List<Long> productIds = List.of(product1.getId(), product2.getId());
		Map<Long, Boolean> expectedStatus = Map.of(
			product1.getId(), true,
			product2.getId(), false
		);
		when(productLikeRepository.existsByMemberIdAndProductIds(member1.getId(), productIds))
			.thenReturn(expectedStatus);

		// when
		Map<Long, Boolean> likedStatus = productLikeService.getLikedStatus(productIds, member1.getId());

		// then
		assertThat(likedStatus).hasSize(2);
		assertTrue(likedStatus.get(product1.getId()));
		assertFalse(likedStatus.get(product2.getId()));
	}

	@Test
	@DisplayName("비회원의 경우 좋아요 여부는 false를 반환한다")
	void 비회원_좋아요_여부_테스트() {
		// given
		Long productId = product1.getId();

		// when
		Boolean isLiked = productLikeService.isLiked(productId, null);

		// then
		assertFalse(isLiked);
		verify(productLikeRepository, never()).findByMemberIdAndProductId(any(), any());
	}

	@Test
	@DisplayName("좋아요가 없는 상품의 개수는 0을 반환한다")
	void 좋아요_없는_상품_개수_테스트() {
		// given
		Long productId = product1.getId();
		when(productLikeRepository.countByProductId(productId)).thenReturn(null);

		// when
		Long likeCount = productLikeService.getLikeCount(productId);

		// then
		assertThat(likeCount).isEqualTo(0L);
	}
}
