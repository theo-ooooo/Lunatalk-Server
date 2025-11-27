package kr.co.lunatalk.domain.payment.service;

import kr.co.lunatalk.domain.order.domain.Order;
import kr.co.lunatalk.domain.order.domain.OrderStatus;
import kr.co.lunatalk.domain.order.repository.OrderRepository;
import kr.co.lunatalk.domain.payment.domain.Payment;
import kr.co.lunatalk.domain.payment.domain.PaymentStatus;
import kr.co.lunatalk.domain.payment.dto.request.PaymentCancelRequest;
import kr.co.lunatalk.domain.payment.dto.request.PaymentConfirmRequest;
import kr.co.lunatalk.domain.payment.dto.response.PaymentCancelResponse;
import kr.co.lunatalk.domain.payment.dto.response.PaymentConfirmResponse;
import kr.co.lunatalk.domain.payment.dto.toss.TossPaymentConfirmRequest;
import kr.co.lunatalk.domain.payment.dto.toss.TossPaymentConfirmResponse;
import kr.co.lunatalk.domain.payment.repository.PaymentRepository;
import kr.co.lunatalk.global.exception.CustomException;
import kr.co.lunatalk.global.exception.ErrorCode;
import kr.co.lunatalk.infra.config.toss.TossPaymentsProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

	private final OrderRepository orderRepository;
	private final PaymentRepository paymentRepository;
	private final TossPaymentsProperties tossPaymentsProperties;
	private final RestClient tossPaymentsRestClient;

	public PaymentConfirmResponse confirmPayment(PaymentConfirmRequest request) {
		Order order = orderRepository.findByOrderWithItems(request.orderId())
			.orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

		if (!Objects.equals(order.getTotalPrice(), request.amount())) {
			throw new CustomException(ErrorCode.PAYMENT_AMOUNT_MISMATCH);
		}

		TossPaymentConfirmRequest tossRequest = new TossPaymentConfirmRequest(
			request.paymentKey(),
			request.orderId(),
			request.amount()
		);

		TossPaymentConfirmResponse tossResponse;
		try {
			tossResponse = tossPaymentsRestClient.post()
				.uri("/v1/payments/confirm")
				.headers(headers -> headers.setBasicAuth(tossPaymentsProperties.secretKey(), ""))
				.body(tossRequest)
				.retrieve()
				.body(TossPaymentConfirmResponse.class);
		} catch (RestClientException e) {
			throw new CustomException(ErrorCode.PAYMENT_CONFIRM_FAILED);
		}

		if (tossResponse == null) {
			throw new CustomException(ErrorCode.PAYMENT_CONFIRM_FAILED);
		}

		Payment payment = Payment.success(
			order,
			tossResponse.paymentKey(),
			tossResponse.orderId(),
			tossResponse.totalAmount(),
			tossResponse.method(),
			tossResponse.getApprovedAtAsLocalDateTime()
		);

		paymentRepository.save(payment);

		order.updateStatus(OrderStatus.PAYMENT_COMPLETED);

		return PaymentConfirmResponse.of(order, payment);
	}

	public PaymentCancelResponse cancelPayment(PaymentCancelRequest request) {
		Payment payment = paymentRepository.findByOrder_OrderNumber(request.orderId())
			.orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND));

		Order order = payment.getOrder();

		try {
			tossPaymentsRestClient.post()
				.uri("/v1/payments/{paymentKey}/cancel", payment.getPaymentKey())
				.headers(headers -> headers.setBasicAuth(tossPaymentsProperties.secretKey(), ""))
				.body(Map.of("cancelReason", request.cancelReason()))
				.retrieve()
				.toBodilessEntity();
		} catch (RestClientException e) {
			throw new CustomException(ErrorCode.PAYMENT_CANCEL_FAILED);
		}

		payment.cancel();
		order.updateStatus(OrderStatus.CANCELLED);

		return PaymentCancelResponse.of(order, payment);
	}
}


