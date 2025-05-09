package kr.co.lunatalk.domain.payment.domain;

import jakarta.persistence.*;
import kr.co.lunatalk.domain.common.domain.BaseTimeEntity;
import kr.co.lunatalk.domain.order.domain.Order;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Payment extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id")
	private Order order;

	@Enumerated(EnumType.STRING)
	private PaymentStatus status;

	private String tId; // 결제키
	private String method; // 결제 수단
	private Long amount; // 결제금액

	@Builder(access = AccessLevel.PRIVATE)
	public Payment(Order order, PaymentStatus status, String tId, String method, Long amount) {
		this.order = order;
		this.status = status;
		this.tId = tId;
		this.method = method;
		this.amount = amount;
	}



	public static Payment createPayment(Order order, String method, Long amount) {
		return Payment.builder()
			.order(order)
			.method(method)
			.amount(amount)
			.status(PaymentStatus.REQUESTED)
			.build();
	}

	public void paymentSuccess(String tId) {
		this.status = PaymentStatus.SUCCESS;
		this.tId = tId;
	}

	public void paymentFail() {
		this.status = PaymentStatus.FAILED;
	}

	public void paymentCancel() {
		this.status = PaymentStatus.CANCELLED;
	}

	public void paymentRefund() {
		this.status = PaymentStatus.REFUNDED;
	}
}
