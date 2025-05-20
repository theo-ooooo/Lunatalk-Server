package kr.co.lunatalk.domain.cartitem.service;

import kr.co.lunatalk.domain.cartitem.domain.CartItem;
import kr.co.lunatalk.domain.cartitem.dto.request.CreateCartItemRequest;
import kr.co.lunatalk.domain.cartitem.dto.request.UpdateCartItemRequest;
import kr.co.lunatalk.domain.cartitem.dto.response.CreateCartItemResponse;
import kr.co.lunatalk.domain.cartitem.repository.CartItemRepository;
import kr.co.lunatalk.domain.image.domain.Image;
import kr.co.lunatalk.domain.member.domain.Member;
import kr.co.lunatalk.domain.member.domain.Profile;
import kr.co.lunatalk.domain.product.domain.Product;
import kr.co.lunatalk.domain.product.domain.ProductStatus;
import kr.co.lunatalk.domain.product.domain.ProductVisibility;
import kr.co.lunatalk.domain.product.dto.ProductWithImagesResult;
import kr.co.lunatalk.global.exception.CustomException;
import kr.co.lunatalk.global.util.MemberUtil;
import kr.co.lunatalk.global.util.ProductUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartItemServiceTest {

	@InjectMocks
	private CartItemService cartItemService;

	@Mock
	private CartItemRepository cartItemRepository;

	@Mock
	private MemberUtil memberUtil;

	@Mock
	private ProductUtil productUtil;

	private Member member;
	private Product product;

	@BeforeEach
	void setUp() {

		member = Member.createMember("TEST", "1234", Profile.of("TEST", ""), "01012341234", "kkwondev@gmail.com");
		ReflectionTestUtils.setField(member, "id", 1L);

		product = Product.createProduct("TEST_PRODUCT", 10000L, 100, ProductStatus.ACTIVE, ProductVisibility.VISIBLE);
		ReflectionTestUtils.setField(product, "id", 100L);
	}

	@Test
	void create_shouldCreateCartItemSuccessfully() {
		// given
		CreateCartItemRequest request = new CreateCartItemRequest(product.getId(), 2);

		when(memberUtil.getCurrentMember()).thenReturn(member);
		when(productUtil.findProductId(product.getId())).thenReturn(product);
		when(cartItemRepository.save(any(CartItem.class)))
			.thenAnswer(invocation -> {
				CartItem saved = invocation.getArgument(0);
				ReflectionTestUtils.setField(saved, "id", 1L);
				return saved;
			});

		// when
		CreateCartItemResponse response = cartItemService.create(request);

		// then
		assertThat(response.cartItemId()).isEqualTo(1L);
	}

	@Test
	void findAll_shouldReturnCartItems() {
		// given
		CartItem cartItem = CartItem.createCartItem(member, product, 3);
		ReflectionTestUtils.setField(cartItem, "id", 1L);

		List<CartItem> cartItems = List.of(cartItem);
		Map<Long, List<Image>> imageMap = Map.of(product.getId(), List.of());

		when(memberUtil.getCurrentMember()).thenReturn(member);
		when(cartItemRepository.findByMemberId(member.getId())).thenReturn(cartItems);
		when(productUtil.findAllProducts(List.of(product.getId())))
			.thenReturn(new ProductWithImagesResult(List.of(product), imageMap));

		// when
		var result = cartItemService.findAll();

		// then
		assertThat(result).hasSize(1);
		assertThat(result.get(0).quantity()).isEqualTo(3);
	}

	@Test
	void deleteById_shouldDeleteCartItem() {
		// given
		CartItem cartItem = CartItem.createCartItem(member, product, 1);
		ReflectionTestUtils.setField(cartItem, "id", 10L);

		when(memberUtil.getCurrentMember()).thenReturn(member);
		when(cartItemRepository.findById(10L)).thenReturn(Optional.of(cartItem));

		// when
		cartItemService.deleteById(10L);

		// then
		verify(cartItemRepository).deleteById(10L);
	}

	@Test
	void updateById_shouldUpdateQuantity() {
		// given
		CartItem cartItem = spy(CartItem.createCartItem(member, product, 1));
		ReflectionTestUtils.setField(cartItem, "id", 5L);

		when(memberUtil.getCurrentMember()).thenReturn(member);
		when(cartItemRepository.findById(5L)).thenReturn(Optional.of(cartItem));

		// when
		cartItemService.updateById(5L, new UpdateCartItemRequest(10));

		// then
		verify(cartItem).updateQuantity(10);
	}

	@Test
	void deleteById_shouldThrow_ifUserMismatch() {
		// given
		Member another = Member.createMember("TEST2", "1234", Profile.of("TEST", ""), "01012341234", "kkwondev@gmail.com");
		ReflectionTestUtils.setField(another, "id", 999L);

		CartItem cartItem = CartItem.createCartItem(another, product, 1);
		ReflectionTestUtils.setField(cartItem, "id", 20L);

		when(memberUtil.getCurrentMember()).thenReturn(member);
		when(cartItemRepository.findById(20L)).thenReturn(Optional.of(cartItem));

		// expect
		assertThrows(CustomException.class, () -> cartItemService.deleteById(20L));
	}

	@Test
	void deleteById_shouldThrow_ifCartItemNotFound() {
		// given
		when(memberUtil.getCurrentMember()).thenReturn(member);
		when(cartItemRepository.findById(99L)).thenReturn(Optional.empty());

		// expect
		assertThrows(CustomException.class, () -> cartItemService.deleteById(99L));
	}
}
