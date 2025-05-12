package kr.co.lunatalk.domain.order.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.co.lunatalk.domain.order.domain.OrderStatus;
import kr.co.lunatalk.domain.order.dto.request.OrderCreateDeliveryRequest;
import kr.co.lunatalk.domain.order.dto.request.OrderCreateRequest;
import kr.co.lunatalk.domain.order.dto.response.OrderCreateResponse;
import kr.co.lunatalk.domain.order.dto.response.OrderFindResponse;
import kr.co.lunatalk.domain.order.dto.response.OrderListResponse;
import kr.co.lunatalk.domain.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
	public OrderFindResponse getOrderByOrderNumber(@PathVariable String orderNumber) {
		return orderService.findOrder(orderNumber);
	}

	// 배송지 정보 등록
	@PatchMapping("/{orderNumber}/delivery")
	@Operation(summary = "주문번호로 배송정보 등록", description = "배송정보를 등록합니다.")
	public ResponseEntity<Void> createDelivery(@PathVariable String orderNumber, @Valid @RequestBody OrderCreateDeliveryRequest request) {
		orderService.createDelivery(orderNumber, request);

		return ResponseEntity.ok().build();
	}

	//관리자용
	@GetMapping()
	@Operation(description = "전체 주문 조회")
	public Page<OrderListResponse> getOrders(
		@RequestParam(required = false) String orderNumber,
		@RequestParam(required = false) OrderStatus status,
		@RequestParam(required = false) String username,
		@RequestParam(required = false) String email,
		@RequestParam(required = false) String nickname,
		@RequestParam(required = false) String phone,
		@PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
		return orderService.findOrders(orderNumber, status, username, email, nickname, phone, pageable);
	}
}
