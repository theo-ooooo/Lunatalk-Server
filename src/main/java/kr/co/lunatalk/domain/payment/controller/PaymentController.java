package kr.co.lunatalk.domain.payment.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.co.lunatalk.domain.payment.dto.request.PaymentWebhookTossRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
@Tag(name = "결제 API", description = "결제 관련 API")
public class PaymentController {


	@PostMapping("/webhook/toss")
	public void webhookToss(@Valid @RequestBody PaymentWebhookTossRequest request) {}
}
