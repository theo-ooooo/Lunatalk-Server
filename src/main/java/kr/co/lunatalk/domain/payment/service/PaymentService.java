package kr.co.lunatalk.domain.payment.service;

import kr.co.lunatalk.domain.order.domain.Order;
import kr.co.lunatalk.domain.order.repository.OrderRepository;
import kr.co.lunatalk.domain.payment.domain.Payment;
import kr.co.lunatalk.domain.payment.domain.PaymentStatus;
import kr.co.lunatalk.domain.payment.dto.request.PaymentCreateRequest;
import kr.co.lunatalk.domain.payment.dto.response.PaymentCreateResponse;
import kr.co.lunatalk.domain.payment.repository.PaymentRepository;
import kr.co.lunatalk.global.exception.CustomException;
import kr.co.lunatalk.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentService {
	private final PaymentRepository paymentRepository;
	private final OrderRepository orderRepository;


	public PaymentCreateResponse create(PaymentCreateRequest request) {
		// 해당 주문이 있는지,
		Order findOrder = orderRepository.findById(request.orderId()).orElseThrow(
			() -> new CustomException(ErrorCode.ORDER_NOT_FOUND)
		);


		// 해당 주문으로 결제건이 있는지.
		Optional<Payment> findPayment = paymentRepository.findByOrderId(findOrder.getId());

		Payment payment;
		// 기존 건이 없다면 생성
		if(findPayment.isEmpty()) {
			payment = Payment.createPayment(findOrder, request.method(), request.amount());
			paymentRepository.save(payment);
		} else {
			payment = findPayment.get();

			// 요청이 아니면 이미 상황종료된 주문건. 새 주문건으로 요청 시도
			if(!payment.getStatus().equals(PaymentStatus.REQUESTED)) {
				throw new CustomException(ErrorCode.PAYMENT_ALREADY_CLOSED);
			}
		}

		return PaymentCreateResponse.of(payment.getId());
	}
}
