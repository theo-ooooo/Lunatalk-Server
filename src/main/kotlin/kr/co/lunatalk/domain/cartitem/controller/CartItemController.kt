package kr.co.lunatalk.domain.cartitem.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import kr.co.lunatalk.domain.cartitem.dto.request.CreateCartItemRequest
import kr.co.lunatalk.domain.cartitem.dto.request.UpdateCartItemRequest
import kr.co.lunatalk.domain.cartitem.dto.response.CartFindResponse
import kr.co.lunatalk.domain.cartitem.dto.response.CreateCartItemResponse
import kr.co.lunatalk.domain.cartitem.service.CartItemService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/cart-items")
@Tag(name = "장바구니", description = "장바구니 관련 API")
class CartItemController(
    private val cartItemService: CartItemService
) {

    @PostMapping
    @Operation(summary = "상품 추가", description = "장바구니에 상품을 추가합니다.")
    fun createCartItem(@Valid @RequestBody request: CreateCartItemRequest): CreateCartItemResponse {
        return cartItemService.create(request)
    }

    @GetMapping
    @Operation(summary = "장바구니 리스트", description = "회원의 장바구니를 확인합니다.")
    fun findAll(): List<CartFindResponse> {
        return cartItemService.findAll()
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "장바구니 삭제", description = "회원의 장바구니를 삭제합니다.")
    fun deleteById(@PathVariable id: Long) {
        cartItemService.deleteById(id)
    }

    @PutMapping("/{id}")
    @Operation(summary = "장바구니를 수정", description = "장바구니를 수정합니다. (갯수)")
    fun updateById(@PathVariable id: Long, @Valid @RequestBody request: UpdateCartItemRequest) {
        cartItemService.updateById(id, request)
    }
}
