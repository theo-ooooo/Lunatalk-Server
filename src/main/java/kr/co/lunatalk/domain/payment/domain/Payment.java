package kr.co.lunatalk.domain.payment.domain;

import jakarta.persistence.*;
import kr.co.lunatalk.domain.common.domain.BaseTimeEntity;
import kr.co.lunatalk.domain.order.domain.Order;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", nullable = false)
	private Order order;

	@Column(nullable = false, unique = true)
	private String paymentKey;

	@Column(nullable = false)
	private String orderNumber;

	@Column(nullable = false)
	private Long amount;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private PaymentStatus status;

	private String method;

	private LocalDateTime approvedAt;

	@Builder(access = AccessLevel.PRIVATE)
	private Payment(Order order, String paymentKey, String orderNumber, Long amount,
		PaymentStatus status, String method, LocalDateTime approvedAt) {
		this.order = order;
		this.paymentKey = paymentKey;
		this.orderNumber = orderNumber;
		this.amount = amount;
		this.status = status;
		this.method = method;
		this.approvedAt = approvedAt;
	}

	public static Payment success(Order order, String paymentKey, String orderNumber, Long amount,
		String method, LocalDateTime approvedAt) {
		return Payment.builder()
			.order(order)
			.paymentKey(paymentKey)
			.orderNumber(orderNumber)
			.amount(amount)
			.status(PaymentStatus.SUCCESS)
			.method(method)
			.approvedAt(approvedAt)
			.build();
	}

	public void cancel() {
		this.status = PaymentStatus.CANCELLED;
	}

	public void fail() {
		this.status = PaymentStatus.FAILED;
	}
}


