package kr.co.lunatalk.domain.order.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import kr.co.lunatalk.domain.order.domain.OrderStatus
import kr.co.lunatalk.domain.order.dto.request.OrderCreateDeliveryRequest
import kr.co.lunatalk.domain.order.dto.request.OrderCreateRequest
import kr.co.lunatalk.domain.order.dto.request.OrderUpdateRequest
import kr.co.lunatalk.domain.order.dto.response.OrderCreateResponse
import kr.co.lunatalk.domain.order.dto.response.OrderFindResponse
import kr.co.lunatalk.domain.order.dto.response.OrderListResponse
import kr.co.lunatalk.domain.order.service.OrderService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/orders")
@Tag(name = "주문", description = "주문 관련 API")
class OrderController(
    private val orderService: OrderService
) {

    @PostMapping
    @Operation(summary = "주문 생성", description = "전달 받은 상품으로 주문을 생성합니다.")
    fun create(@Valid @RequestBody request: OrderCreateRequest): OrderCreateResponse {
        return orderService.createOrder(request)
    }

    @GetMapping("/{orderNumber}")
    @Operation(summary = "주문번호로 주문 조회", description = "주문 번호로 주문을 조회합니다.")
    fun getOrderByOrderNumber(@PathVariable orderNumber: String): OrderFindResponse {
        return orderService.findOrder(orderNumber)
    }

    @PatchMapping("/{orderNumber}/delivery")
    @Operation(summary = "주문번호로 배송정보 등록", description = "배송정보를 등록합니다.")
    fun createDelivery(
        @PathVariable orderNumber: String,
        @Valid @RequestBody request: OrderCreateDeliveryRequest
    ): ResponseEntity<Void> {
        orderService.createDelivery(orderNumber, request)
        return ResponseEntity.ok().build()
    }

    @GetMapping
    @Operation(summary = "전체 주문 조회", description = "전체 주문 조회합니다.")
    @PreAuthorize("hasRole('ADMIN')")
    fun getOrders(
        @RequestParam(required = false) orderNumber: String?,
        @RequestParam(required = false) status: OrderStatus?,
        @RequestParam(required = false) username: String?,
        @RequestParam(required = false) email: String?,
        @RequestParam(required = false) nickname: String?,
        @RequestParam(required = false) phone: String?,
        @PageableDefault(size = 10, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable
    ): Page<OrderListResponse> {
        return orderService.findOrders(orderNumber, status, username, email, nickname, phone, pageable)
    }

    @PatchMapping("/{orderNumber}")
    @Operation(summary = "주문 정보 수정", description = "주문 정보를 수정합니다.")
    @PreAuthorize("hasRole('ADMIN')")
    fun updateOrder(
        @PathVariable orderNumber: String,
        @Valid @RequestBody request: OrderUpdateRequest
    ) {
        orderService.updateOrder(orderNumber, request)
    }
}
