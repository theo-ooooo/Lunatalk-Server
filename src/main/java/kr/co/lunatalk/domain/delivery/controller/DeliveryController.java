package kr.co.lunatalk.domain.delivery.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.co.lunatalk.domain.delivery.dto.request.DeliveryUpdateRequest;
import kr.co.lunatalk.domain.delivery.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/deliveries")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "배송", description = "배송 관련 API")
public class DeliveryController {
	private final DeliveryService deliveryService;

	@PatchMapping("/{id}")
	@Operation(summary = "배송 정보 변경", description = "배송 정보를 변경합니다. (운송장번호, 배송 상태)")
	public void updateDelivery(@PathVariable Long id, @Valid @RequestBody DeliveryUpdateRequest request) {
		deliveryService.update(id, request);
	}
}
