package kr.co.lunatalk.domain.cartitem.service;

import kr.co.lunatalk.domain.cartitem.domain.CartItem;
import kr.co.lunatalk.domain.cartitem.dto.response.CartFindResponse;
import kr.co.lunatalk.domain.cartitem.dto.request.CreateCartItemRequest;
import kr.co.lunatalk.domain.cartitem.dto.request.UpdateCartItemRequest;
import kr.co.lunatalk.domain.cartitem.dto.response.CreateCartItemResponse;
import kr.co.lunatalk.domain.cartitem.repository.CartItemRepository;
import kr.co.lunatalk.domain.image.domain.Image;
import kr.co.lunatalk.domain.member.domain.Member;
import kr.co.lunatalk.domain.product.domain.Product;
import kr.co.lunatalk.domain.product.dto.FindProductDto;
import kr.co.lunatalk.domain.product.dto.ProductWithImagesResult;
import kr.co.lunatalk.domain.productlike.service.ProductLikeService;
import kr.co.lunatalk.global.exception.CustomException;
import kr.co.lunatalk.global.exception.ErrorCode;
import kr.co.lunatalk.global.util.MemberUtil;
import kr.co.lunatalk.global.util.ProductUtil;
import kr.co.lunatalk.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class CartItemService {
	private final CartItemRepository cartItemRepository;
	private final MemberUtil memberUtil;
	private final ProductUtil productUtil;
	private final ProductLikeService productLikeService;
	private final SecurityUtil securityUtil;


	public CreateCartItemResponse create(CreateCartItemRequest request) {
		Member member = memberUtil.getCurrentMember();
		Product product = productUtil.findProductId(request.productId());

		CartItem cartItem = CartItem.createCartItem(member, product, request.quantity());

		cartItemRepository.save(cartItem);

		return CreateCartItemResponse.from(cartItem);
	}

	public List<CartFindResponse> findAll() {
		Member member = memberUtil.getCurrentMember();

		List<CartItem> cartItems = cartItemRepository.findByMemberId(member.getId());

		List<Long> productIds = cartItems.stream().map(cartItem -> cartItem.getProduct().getId()).toList();

		ProductWithImagesResult productWithImagesResult = productUtil.findAllProducts(productIds);

		List<Product> products = productWithImagesResult.products();
		Map<Long, List<Image>> imageMap = productWithImagesResult.imageMap();

		Map<Long, Long> likeCountMap = productLikeService.getLikeCounts(productIds);
		Long currentMemberId = getCurrentMemberId();
		Map<Long, Boolean> likedStatusMap = productLikeService.getLikedStatus(productIds, currentMemberId);

		return cartItems.stream().map(cartItem -> {
			Optional<Product> findProduct = products.stream()
				.filter(product -> product.getId().equals(cartItem.getProduct().getId()))
				.findFirst();

			if(findProduct.isEmpty()) {
				return null;
			}
			Product product = findProduct.get();
			Long likeCount = likeCountMap.getOrDefault(product.getId(), 0L);
			Boolean isLiked = likedStatusMap.getOrDefault(product.getId(), false);
			return CartFindResponse.of(cartItem, FindProductDto.from(product, imageMap.getOrDefault(product.getId(), List.of()), likeCount, isLiked));
		}).filter(Objects::nonNull).toList();
	}

	public void deleteById(Long id) {
		CartItem cartItem = geyMyCartItem(id);
		cartItemRepository.deleteById(cartItem.getId());
	}

	public void updateById(Long id, UpdateCartItemRequest request) {
		CartItem cartItem = geyMyCartItem(id);
		cartItem.updateQuantity(request.quantity());
	}


	private CartItem geyMyCartItem(Long id) {
		Member member = memberUtil.getCurrentMember();

		CartItem cartItem = cartItemRepository.findById(id).orElseThrow(
			() -> new CustomException(ErrorCode.CART_ITEM_NOT_FOUND)
		);

		Member cartItemMember = cartItem.getMember();

		if(!member.getId().equals(cartItemMember.getId())) {
			throw new CustomException(ErrorCode.CART_ITEM_NOT_FOUND);
		}
		return cartItem;
	}

	private Long getCurrentMemberId() {
		try {
			return securityUtil.getCurrentMemberId();
		} catch (Exception e) {
			return null; // 비회원인 경우
		}
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void deleteCartItemByMemberIdAndProductId(Long memberId, Long productId) {
		cartItemRepository.deleteByMemberIdAndProductId(memberId, productId);

	}
}
