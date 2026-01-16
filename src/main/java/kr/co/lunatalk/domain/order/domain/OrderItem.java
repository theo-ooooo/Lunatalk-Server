package kr.co.lunatalk.domain.order.domain;

import jakarta.persistence.*;
import kr.co.lunatalk.domain.common.domain.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem extends BaseTimeEntity {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	//N:1
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id")
	private Order order;

	// 상품 정보는 주문 당시 스냅샷으로 저장
	@Column(nullable = false)
	private Long productId;
	@Column(nullable = false)
	private String productName;

	@Column(nullable = false)
	private Long price;
	@Column(nullable = false)
	private Integer quantity;
	@Column(nullable = false)
	private Long totalPrice;

	@Embedded
	private OptionSnapshot optionSnapshot; // 옵션 정보 (JSON이나 문자열로 저장)

	@Builder
	public OrderItem(Order order, Long productId, String productName, Long price, int quantity, Long totalPrice, OptionSnapshot optionSnapshot) {
		this.order = order;
		this.productId = productId;
		this.productName = productName;
		this.price = price;
		this.quantity = quantity;
		this.totalPrice = totalPrice;
		this.optionSnapshot = optionSnapshot;
	}

	public static OrderItem createOrderItem(Order order, Long productId, String productName, Long price, Integer quantity, Long totalPrice, OptionSnapshot optionSnapshot) {
		return OrderItem.builder()
			.order(order)
			.productId(productId)
			.productName(productName)
			.price(price)
			.quantity(quantity)
			.totalPrice(totalPrice)
			.optionSnapshot(optionSnapshot)
			.build();
	}
}
