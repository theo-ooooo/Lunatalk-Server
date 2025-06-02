package kr.co.lunatalk.domain.payment.service;

import kr.co.lunatalk.domain.member.domain.Member;
import kr.co.lunatalk.domain.order.domain.Order;
import kr.co.lunatalk.domain.payment.domain.Payment;
import kr.co.lunatalk.domain.payment.domain.PaymentStatus;
import kr.co.lunatalk.domain.payment.dto.request.PaymentCreateRequest;
import kr.co.lunatalk.domain.payment.dto.request.PaymentTossConfirmRequest;
import kr.co.lunatalk.domain.payment.dto.response.PaymentCreateResponse;
import kr.co.lunatalk.domain.payment.repository.PaymentRepository;
import kr.co.lunatalk.global.exception.CustomException;
import kr.co.lunatalk.global.exception.ErrorCode;
import kr.co.lunatalk.global.util.MemberUtil;
import kr.co.lunatalk.global.util.OrderUtil;
import kr.co.lunatalk.infra.config.payment.TossProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class PaymentService {
	private final PaymentRepository paymentRepository;
	private final MemberUtil memberUtil;
	private final OrderUtil orderUtil;
	private final TossProperties tossProperties;


	public PaymentCreateResponse create(PaymentCreateRequest request) {
		Member member = memberUtil.getCurrentMember();
		Order order = orderUtil.getOrderByOrderId(request.orderId());

		Payment payment = Payment.createPayment(order, member, request.method(), "toss");
		paymentRepository.save(payment);

		return PaymentCreateResponse.from(payment);
	}
	public void tossConfirm(PaymentTossConfirmRequest request) {
		Order order = orderUtil.getOrderByOrderId(request.orderId());
		String requestBody = createConfirmRequestBody(request, order);

		HttpRequest httpRequest = HttpRequest.newBuilder()
			.uri(URI.create("https://api.tosspayments.com/v1/payments/confirm"))
			.timeout(Duration.ofSeconds(10))
			.header("Authorization", createAuthorizationHeader())
			.header("Content-Type", "application/json")
			.POST(HttpRequest.BodyPublishers.ofString(requestBody))
			.build();

		try {
			HttpClient client = HttpClient.newHttpClient();
			HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

			if (response.statusCode() == 200) {
				confirmSuccess(order, request.paymentKey());
			} else {
				logAndThrowConfirmFailure(response.statusCode(), response.body());
			}
		} catch (Exception e) {
			throw new CustomException(ErrorCode.PAYMENT_CONFIRM_FAIL);
		}
	}

	private String createAuthorizationHeader() {
		String secretKey = tossProperties.secretKey() + ":";
		String encodedKey = Base64.getEncoder().encodeToString(secretKey.getBytes(StandardCharsets.UTF_8));
		return "Basic " + encodedKey;
	}

	private String createConfirmRequestBody(PaymentTossConfirmRequest request, Order order) {
		return """
        {
          "paymentKey": "%s",
          "orderId": "%s",
          "amount": %d
        }
        """.formatted(
			request.paymentKey(),
			request.orderId(),
			order.getTotalPrice()
		);
	}

	private void confirmSuccess(Order order, String paymentKey) {
		Payment payment = paymentRepository.findByOrderId(order.getId())
			.orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND));

		if (payment.getStatus() == PaymentStatus.DONE) {
			throw new CustomException(ErrorCode.PAYMENT_EXISTS_CONFIRM);
		}

		payment.successPayment(paymentKey);
	}

	private void logAndThrowConfirmFailure(int statusCode, String responseBody) {
		log.info("결제 승인 실패 ({}}): {}", statusCode, responseBody);
		throw new CustomException(ErrorCode.PAYMENT_CONFIRM_FAIL);
	}


}
