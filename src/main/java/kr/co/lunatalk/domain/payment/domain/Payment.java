package kr.co.lunatalk.domain.payment.domain;


import jakarta.persistence.*;
import kr.co.lunatalk.domain.common.domain.BaseTimeEntity;
import kr.co.lunatalk.domain.member.domain.Member;
import kr.co.lunatalk.domain.order.domain.Order;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseTimeEntity {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true)
	private String paymentKey;

	@Column(nullable = false)
	private Integer amount;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", nullable = false)
	private Order order;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private PaymentStatus status;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private PaymentMethod method;

	@Builder
	public Payment(Integer amount, Order order, Member member, PaymentStatus status, PaymentMethod method) {
		this.amount = amount;
		this.order = order;
		this.member = member;
		this.status = status;
		this.method = method;
	}

	public static Payment createPayment(Order order, Member member, PaymentMethod method) {
		return Payment.builder()
			.amount(Math.toIntExact(order.getTotalPrice()))
			.order(order)
			.member(member)
			.method(method)
			.status(PaymentStatus.READY)
			.build();
	}

	public void successPayment(String paymentKey) {
		this.status = PaymentStatus.DONE;
		this.paymentKey = paymentKey;
	}

	public void cancelPayment() {
		this.status = PaymentStatus.CANCELED;
	}
}
