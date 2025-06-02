package kr.co.lunatalk.domain.payment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.co.lunatalk.domain.payment.dto.request.PaymentCreateRequest;
import kr.co.lunatalk.domain.payment.dto.response.PaymentCreateResponse;
import kr.co.lunatalk.domain.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Tag(name = "결제", description = "결제 관련 API")
public class PaymentController {
	private final PaymentService paymentService;


	// 결제 생성
	@PostMapping()
	@Operation(summary = "결제 생성", description = "전달받은 결제 정보로 결제를 생성합니다.")
	public PaymentCreateResponse create(@Valid @RequestBody PaymentCreateRequest request) {
		return paymentService.create(request);
	}
}
