package kr.co.lunatalk.domain.order.service;

import kr.co.lunatalk.domain.cartitem.service.CartItemService;
import kr.co.lunatalk.domain.delivery.domain.Delivery;
import kr.co.lunatalk.domain.delivery.dto.response.DeliveryFindResponse;
import kr.co.lunatalk.domain.delivery.repository.DeliveryRepository;
import kr.co.lunatalk.domain.image.domain.Image;
import kr.co.lunatalk.domain.image.domain.ImageType;
import kr.co.lunatalk.domain.image.repository.ImageRepository;
import kr.co.lunatalk.domain.member.domain.Member;
import kr.co.lunatalk.domain.member.domain.MemberRole;
import kr.co.lunatalk.domain.member.dto.response.MemberInfoResponse;
import kr.co.lunatalk.domain.order.domain.OptionSnapshot;
import kr.co.lunatalk.domain.order.domain.Order;
import kr.co.lunatalk.domain.order.domain.OrderItem;
import kr.co.lunatalk.domain.order.domain.OrderStatus;
import kr.co.lunatalk.domain.order.dto.request.OrderCreateDeliveryRequest;
import kr.co.lunatalk.domain.order.dto.request.OrderCreateRequest;
import kr.co.lunatalk.domain.order.dto.request.OrderProductRequest;
import kr.co.lunatalk.domain.order.dto.request.OrderUpdateRequest;
import kr.co.lunatalk.domain.order.dto.response.OrderCreateResponse;
import kr.co.lunatalk.domain.order.dto.response.OrderFindResponse;
import kr.co.lunatalk.domain.order.dto.response.OrderItemResponse;
import kr.co.lunatalk.domain.order.dto.response.OrderListResponse;
import kr.co.lunatalk.domain.order.repository.OrderRepository;
import kr.co.lunatalk.domain.product.domain.Product;
import kr.co.lunatalk.domain.product.repository.ProductRepository;
import kr.co.lunatalk.global.exception.CustomException;
import kr.co.lunatalk.global.exception.ErrorCode;
import kr.co.lunatalk.global.util.MemberUtil;
import kr.co.lunatalk.global.util.OrderUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
	private final OrderRepository orderRepository;
	private final ProductRepository productRepository;
	private final ImageRepository imageRepository;
	private final DeliveryRepository deliveryRepository;
	private final CartItemService cartItemService;
	private final OrderUtil orderUtil;
	private final MemberUtil memberUtil;

	public OrderCreateResponse createOrder(OrderCreateRequest request) {
		Member member = memberUtil.getCurrentMember();
		String orderNumber = orderUtil.generateOrderNumber();

		Order order = Order.createOrder(orderNumber, member, 0L);

		long totalPrice = 0L;

		for (OrderProductRequest p : request.products()) {
			Product product = productRepository.findById(p.productId())
				.orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

			if (product.getQuantity() <= 0) {
				cartItemService.deleteCartItemByMemberIdAndProductId(member.getId(), p.productId());
				throw new CustomException(ErrorCode.PRODUCT_SOLD_OUT);
			}

//			boolean colorExists = product.getProductColor().stream()
//				.anyMatch(c -> c.getColor().equalsIgnoreCase(p.optionSnapshot().getColor()));
//
//			if (!colorExists) {
//				throw new CustomException(ErrorCode.PRODUCT_NOT_FOUND);
//			}

			long price = product.getPrice();
			int quantity = p.quantity();
			long itemTotal = price * quantity;

			String color = p.optionSnapshot().getColor().isEmpty() ? "DEFAULT" : p.optionSnapshot().getColor();
			OptionSnapshot optionSnapshot = OptionSnapshot.createOptionSnapshot(color);

			OrderItem orderItem = OrderItem.createOrderItem(
				order,
				p.productId(),
				product.getName(),
				price,
				quantity,
				itemTotal,
				optionSnapshot
			);

			order.addOrderItem(orderItem);
			totalPrice += itemTotal;
		}

		order.updateTotalPrice(totalPrice);

		orderRepository.save(order);

		return OrderCreateResponse.of(order.getOrderNumber(), order.getId());
	}

	@Transactional(readOnly = true)
	public OrderFindResponse findOrder(String orderNumber) {
		Order findOrder = findOrderWithOrderItemsByOrderNumber(orderNumber);



		if(findOrder == null) {
			throw new CustomException(ErrorCode.ORDER_NOT_FOUND);
		}

		// 검증 내꺼 주문이 맞는지
		boolean isMyOrder = isMyOrder(findOrder);

		if(!isMyOrder) {
			throw new CustomException(ErrorCode.ORDER_NOT_FOUND);
		}

		return toOrderFindResponseWithProductImages(findOrder);
	}

	@Transactional(readOnly = true)
	public Page<OrderFindResponse> findOrdersByMemberId(Long memberId, Pageable pageable) {
		Page<Order> orders = orderRepository.findOrdersWithItemsByMemberId(memberId, pageable);

		return orders.map(OrderFindResponse::from);
	}

	@Transactional(readOnly = true)
	public Page<OrderListResponse> findOrders(
		String orderNumber,
		OrderStatus orderStatus,
		String username,
		String email,
		String nickname,
		String phone,
		Pageable pageable
	) {
		Page<Order> orders = orderRepository.findOrders(orderNumber, orderStatus, username, email, nickname, phone, pageable);

		return orders.map(OrderListResponse::from);
	}



	public void createDelivery(String OrderNumber, OrderCreateDeliveryRequest request) {
		Order findOrder = findOrderWithOrderItemsByOrderNumber(OrderNumber);

		if(findOrder == null) {
			throw new CustomException(ErrorCode.ORDER_NOT_FOUND);
		}
		// 검증 내꺼 주문이 맞는지
		boolean isMyOrder = isMyOrder(findOrder);
		if(!isMyOrder) {
			throw new CustomException(ErrorCode.ORDER_NOT_FOUND);
		}
		Delivery delivery = Delivery.createDelivery(findOrder, request.name(), request.phoneNumber(), request.address1(), request.address2(), request.zipCode(), request.message());

		deliveryRepository.save(delivery);
	}
	public void updateOrder(String orderNumber, OrderUpdateRequest request) {
		Order order = findOrderWithOrderItemsByOrderNumber(orderNumber);

		order.updateStatus(request.status());
	}

	private Order findOrderWithOrderItemsByOrderNumber(String orderNumber) {
		return orderRepository.findByOrderWithItems(orderNumber).orElseThrow(
			() -> new CustomException(ErrorCode.ORDER_NOT_FOUND)
		);
	}

	private OrderFindResponse toOrderFindResponseWithProductImages(Order order) {
		List<Long> productIds = order.getOrderItems().stream()
			.map(OrderItem::getProductId)
			.distinct()
			.toList();

		Map<Long, String> productIdToThumbnailPath = buildProductThumbnailMap(productIds);

		List<OrderItemResponse> orderItems = order.getOrderItems().stream()
			.map(item -> OrderItemResponse.from(item, productIdToThumbnailPath.get(item.getProductId())))
			.toList();

		return new OrderFindResponse(
			order.getId(),
			order.getOrderNumber(),
			order.getStatus().getValue(),
			order.getTotalPrice(),
			orderItems,
			order.getDeliverys().stream().map(DeliveryFindResponse::from).toList(),
			MemberInfoResponse.from(order.getMember()),
			order.getCreatedAt()
		);
	}

	private Map<Long, String> buildProductThumbnailMap(List<Long> productIds) {
		if (productIds == null || productIds.isEmpty()) {
			return Map.of();
		}

		List<Image> images = imageRepository.fetchProductImagesByProductIds(productIds);

		// PRODUCT_THUMBNAIL 우선, 없으면 어떤 이미지든(imageOrder가 가장 작은 것) fallback
		Map<Long, List<Image>> imageMap = images.stream()
			.collect(Collectors.groupingBy(Image::getReferenceId));

		return imageMap.entrySet().stream()
			.collect(Collectors.toMap(
				Map.Entry::getKey,
				entry -> pickThumbnailPath(entry.getValue())
			));
	}

	private String pickThumbnailPath(List<Image> images) {
		if (images == null || images.isEmpty()) {
			return null;
		}

		return images.stream()
			.sorted(Comparator
				.comparing((Image img) -> img.getImageType() == ImageType.PRODUCT_THUMBNAIL ? 0 : 1)
				.thenComparing(Image::getImageOrder, Comparator.nullsLast(Comparator.naturalOrder()))
			)
			.map(Image::getImagePath)
			.findFirst()
			.orElse(null);
	}




	private boolean isMyOrder(Order order) {
		Member currentMember = memberUtil.getCurrentMember();

		boolean isAdmin = currentMember.getRole().equals(MemberRole.ADMIN);

		return isAdmin || order.getMember().getId().equals(currentMember.getId());
	}
}
