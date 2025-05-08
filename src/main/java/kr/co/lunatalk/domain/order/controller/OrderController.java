package kr.co.lunatalk.domain.order.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.co.lunatalk.domain.order.domain.Order;
import kr.co.lunatalk.domain.order.dto.request.OrderCreateRequest;
import kr.co.lunatalk.domain.order.dto.response.OrderCreateResponse;
import kr.co.lunatalk.domain.order.dto.response.OrderFIndResponse;
import kr.co.lunatalk.domain.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Tag(name = "주문", description = "주문 관련 API")
public class OrderController {
	private final OrderService orderService;

	// 주문 생성
	@PostMapping()
	@Operation(summary = "주문 생성", description = "전달 받은 상품으로 주문을 생성합니다.")
	public OrderCreateResponse create(@Valid @RequestBody OrderCreateRequest request) {
		return orderService.createOrder(request);
	}

	// 주문 조회
	@GetMapping("/{orderNumber}")
	@Operation(summary = "주문번호로 주문 조회", description = "주문 번호로 주문을 조회합니다.")
	public OrderFIndResponse getOrderByOrderNumber(@PathVariable("orderNumber") String orderNumber) {
		return orderService.findOrder(orderNumber);
	}

	// 배송지 정보 등록
	@PatchMapping("/{orderNumber}/delivery")
	@Operation(summary = "주문번호로 배송정보 등록", description = "배송정보를 등록합니다.")
	public ResponseEntity<Void> createDelivery() {
		return ResponseEntity.ok().build();
	}
}
