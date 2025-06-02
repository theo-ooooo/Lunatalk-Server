package kr.co.lunatalk.domain.payment.service;

import kr.co.lunatalk.domain.member.domain.Member;
import kr.co.lunatalk.domain.order.domain.Order;
import kr.co.lunatalk.domain.payment.domain.Payment;
import kr.co.lunatalk.domain.payment.dto.request.PaymentCreateRequest;
import kr.co.lunatalk.domain.payment.dto.response.PaymentCreateResponse;
import kr.co.lunatalk.domain.payment.repository.PaymentRepository;
import kr.co.lunatalk.global.util.MemberUtil;
import kr.co.lunatalk.global.util.OrderUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentService {
	private final PaymentRepository paymentRepository;
	private final MemberUtil memberUtil;
	private final OrderUtil orderUtil;


	public PaymentCreateResponse create(PaymentCreateRequest request) {
		Member member = memberUtil.getCurrentMember();
		Order order = orderUtil.getOrderByOrderId(request.orderId());

		Payment payment = Payment.createPayment(order, member, request.method());
		paymentRepository.save(payment);

		return PaymentCreateResponse.from(payment);
	}
}
