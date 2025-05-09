package kr.co.lunatalk.domain.order.domain;

import jakarta.persistence.*;
import kr.co.lunatalk.domain.common.domain.BaseTimeEntity;
import kr.co.lunatalk.domain.delivery.domain.Delivery;
import kr.co.lunatalk.domain.member.domain.Member;
import kr.co.lunatalk.domain.payment.domain.Payment;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseTimeEntity {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String orderNumber;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	private Long totalPrice;

	@Enumerated(EnumType.STRING)
	private OrderStatus status;

	@OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
	private Payment payment;

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<OrderItem> orderItems = new ArrayList<>();

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
	private List<Delivery> deliveries = new ArrayList<>();

	@Builder(access = AccessLevel.PRIVATE)
	public Order(String orderNumber, Member member, Long totalPrice, OrderStatus status) {
		this.orderNumber = orderNumber;
		this.member = member;
		this.totalPrice = totalPrice;
		this.status = status;
	}

	public static Order createOrder(String orderNumber, Member member, Long totalPrice) {
		return Order.builder()
			.orderNumber(orderNumber)
			.member(member)
			.totalPrice(totalPrice)
			.status(OrderStatus.CREATED)
			.build();
	}

	public void addOrderItem(OrderItem orderItem) {
		orderItems.add(orderItem);
	}

	public void updateTotalPrice(long totalPrice) {
		this.totalPrice = totalPrice;
	}

	public void updateStatus(OrderStatus status) {
		this.status = status;
	}
}
