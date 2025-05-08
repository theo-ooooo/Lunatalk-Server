package kr.co.lunatalk.domain.order.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import kr.co.lunatalk.domain.member.domain.Member;
import kr.co.lunatalk.domain.member.domain.MemberRole;
import kr.co.lunatalk.domain.member.domain.Profile;
import kr.co.lunatalk.domain.member.repository.MemberRepository;
import kr.co.lunatalk.domain.order.domain.OptionSnapshot;
import kr.co.lunatalk.domain.order.domain.Order;
import kr.co.lunatalk.domain.order.dto.request.OrderCreateRequest;
import kr.co.lunatalk.domain.order.dto.request.OrderProductRequest;
import kr.co.lunatalk.domain.order.dto.response.OrderCreateResponse;
import kr.co.lunatalk.domain.order.repository.OrderRepository;
import kr.co.lunatalk.domain.product.domain.Product;
import kr.co.lunatalk.domain.product.domain.ProductColor;
import kr.co.lunatalk.domain.product.domain.ProductStatus;
import kr.co.lunatalk.domain.product.domain.ProductVisibility;
import kr.co.lunatalk.domain.product.repository.ProductRepository;
import kr.co.lunatalk.global.exception.CustomException;
import kr.co.lunatalk.global.security.PrincipalDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class OrderServiceTest {

	@Autowired
	OrderService orderService;
	@Autowired
	MemberRepository memberRepository;
	@Autowired
	ProductRepository productRepository;
	@Autowired
	OrderRepository orderRepository;

	@PersistenceContext
	EntityManager em;

	private Member member;
	private Product product;

	@BeforeEach
	void setup() {
		member = Member.createMember("testuser", "1234", Profile.of("테스트닉", "img"));
		memberRepository.save(member);

		product = Product.createProduct("테스트", 5000L, 1, ProductStatus.ACTIVE, ProductVisibility.VISIBLE);
		ProductColor productColor = ProductColor.createProductColor(product, "blue");
		product.addProductColor(productColor);
		productRepository.save(product);

		em.flush();
		em.clear();

		PrincipalDetails principalDetails = new PrincipalDetails(member.getId(), MemberRole.ADMIN);
		SecurityContextHolder.getContext().setAuthentication(
			new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities())
		);
	}

	@Test
	void 주문_생성_테스트() {
		// given
		OrderCreateRequest request = new OrderCreateRequest(List.of(
			new OrderProductRequest(product.getId(), 2, new OptionSnapshot("blue"))
		));

		// when
		OrderCreateResponse response = orderService.createOrder(request);

		// then
		assertNotNull(response.orderNumber());
		assertNotNull(response.orderId());

		Optional<Order> order = orderRepository.findByOrderWithItems(response.orderNumber());

		assertNotNull(order.get());
		assertEquals(1, order.get().getOrderItems().size());
		assertEquals(5000L * 2, order.get().getTotalPrice());
	}

	@Test
	void 존재하지_않는_상품으로_주문_시_예외() {
		// given
		OrderCreateRequest request = new OrderCreateRequest(List.of(
			new OrderProductRequest(-1L, 1, new OptionSnapshot("blue"))
		));

		// when & then
		assertThrows(CustomException.class, () -> orderService.createOrder(request));
	}

	@Test
	void 존재하지_않는_컬러로_주문_시_예외() {
		// given
		OrderCreateRequest request = new OrderCreateRequest(List.of(
			new OrderProductRequest(product.getId(), 1, new OptionSnapshot("red")) // 등록된 컬러는 blue
		));

		// when & then
		assertThrows(CustomException.class, () -> orderService.createOrder(request));
	}



}
