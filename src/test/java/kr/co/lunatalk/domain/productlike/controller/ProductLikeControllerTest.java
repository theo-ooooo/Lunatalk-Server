package kr.co.lunatalk.domain.productlike.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import kr.co.lunatalk.global.security.JwtTokenProvider;
import kr.co.lunatalk.global.security.PrincipalDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class ProductLikeControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	ProductLikeRepository productLikeRepository;

	@Autowired
	MemberRepository memberRepository;

	@Autowired
	ProductRepository productRepository;

	@Autowired
	JwtTokenProvider jwtTokenProvider;

	@Autowired
	kr.co.lunatalk.domain.productlike.service.ProductLikeService productLikeService;

	@PersistenceContext
	EntityManager em;

	private Member member;
	private Product product;
	private String accessToken;

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

		product = Product.createProduct("테스트상품", 10000L, 10, ProductStatus.ACTIVE, ProductVisibility.VISIBLE);
		ProductColor color = ProductColor.createProductColor(product, "red");
		product.addProductColor(color);
		productRepository.save(product);

		// JWT 토큰 생성
		var tokenResponse = jwtTokenProvider.generateTokenPair(member.getId(), MemberRole.USER);
		accessToken = tokenResponse.accessToken();
	}

	@Test
	@DisplayName("인증된 사용자는 상품에 좋아요를 누를 수 있다")
	void 좋아요_추가_API_테스트() throws Exception {
		// given
		Long productId = product.getId();

		// when & then
		mockMvc.perform(post("/products/{productId}/likes", productId)
				.header("Authorization", "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());

		// 검증
		Optional<ProductLike> productLike = productLikeRepository.findByMemberIdAndProductId(
			member.getId(), productId
		);
		assertTrue(productLike.isPresent());
	}

	@Test
	@DisplayName("이미 좋아요를 누른 상품에 다시 요청하면 좋아요가 취소된다")
	void 좋아요_취소_API_테스트() throws Exception {
		// given
		Long productId = product.getId();
		productLikeService.toggleLike(productId); // 좋아요 추가

		// when & then
		mockMvc.perform(post("/products/{productId}/likes", productId)
				.header("Authorization", "Bearer " + accessToken)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());

		// 검증
		Optional<ProductLike> productLike = productLikeRepository.findByMemberIdAndProductId(
			member.getId(), productId
		);
		assertFalse(productLike.isPresent());
	}

	@Test
	@DisplayName("인증되지 않은 사용자는 좋아요를 누를 수 없다")
	void 비인증_사용자_좋아요_실패_테스트() throws Exception {
		// given
		Long productId = product.getId();

		// when & then
		mockMvc.perform(post("/products/{productId}/likes", productId)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnauthorized());
	}
}

