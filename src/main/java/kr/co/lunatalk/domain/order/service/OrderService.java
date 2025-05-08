package kr.co.lunatalk.domain.order.service;

import kr.co.lunatalk.domain.member.domain.Member;
import kr.co.lunatalk.domain.order.domain.OptionSnapshot;
import kr.co.lunatalk.domain.order.domain.Order;
import kr.co.lunatalk.domain.order.domain.OrderItem;
import kr.co.lunatalk.domain.order.dto.request.OrderCreateRequest;
import kr.co.lunatalk.domain.order.dto.request.OrderProductRequest;
import kr.co.lunatalk.domain.order.dto.response.OrderCreateResponse;
import kr.co.lunatalk.domain.order.dto.response.OrderFIndResponse;
import kr.co.lunatalk.domain.order.repository.OrderRepository;
import kr.co.lunatalk.domain.product.domain.Product;
import kr.co.lunatalk.domain.product.repository.ProductRepository;
import kr.co.lunatalk.global.exception.CustomException;
import kr.co.lunatalk.global.exception.ErrorCode;
import kr.co.lunatalk.global.util.MemberUtil;
import kr.co.lunatalk.global.util.OrderUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
	private final OrderRepository orderRepository;
	private final ProductRepository productRepository;
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

			boolean colorExists = product.getProductColor().stream()
				.anyMatch(c -> c.getColor().equalsIgnoreCase(p.optionSnapshot().getColor()));

			if (!colorExists) {
				throw new CustomException(ErrorCode.PRODUCT_NOT_FOUND);
			}

			long price = product.getPrice();
			int quantity = p.quantity();
			long itemTotal = price * quantity;

			OptionSnapshot optionSnapshot = OptionSnapshot.createOptionSnapshot(p.optionSnapshot().getColor());

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
	public OrderFIndResponse findOrder(String orderNumber) {
		Order findOrder = orderRepository.findByOrderWithItems(orderNumber);

		// 검증 내꺼 주문이 맞는지
		boolean isMyOrder = isMyOrder(findOrder);

		if(findOrder == null ||  !isMyOrder) {
			throw new CustomException(ErrorCode.ORDER_NOT_FOUND);
		}

		return OrderFIndResponse.from(findOrder);
	}

	@Transactional(readOnly = true)
	public boolean isMyOrder(Order order) {
		Member currentMember = memberUtil.getCurrentMember();

		return order.getMember().getId().equals(currentMember.getId());
	}
}
