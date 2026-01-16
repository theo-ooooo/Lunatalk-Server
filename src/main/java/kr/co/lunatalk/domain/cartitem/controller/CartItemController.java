package kr.co.lunatalk.domain.cartitem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.co.lunatalk.domain.cartitem.dto.response.CartFindResponse;
import kr.co.lunatalk.domain.cartitem.dto.request.CreateCartItemRequest;
import kr.co.lunatalk.domain.cartitem.dto.request.UpdateCartItemRequest;
import kr.co.lunatalk.domain.cartitem.dto.response.CreateCartItemResponse;
import kr.co.lunatalk.domain.cartitem.service.CartItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart-items")
@RequiredArgsConstructor
@Tag(name = "장바구니", description = "장바구니 관련 API")
public class CartItemController {
	private final CartItemService cartItemService;

	@PostMapping()
	@Operation(summary = "상품 추가", description = "장바구니에 상품을 추가합니다.")
	public CreateCartItemResponse createCartItem(@Valid @RequestBody CreateCartItemRequest request) {
		return cartItemService.create(request);
	}

	@GetMapping()
	@Operation(summary = "장바구니 리스트", description = "회원의 장바구니를 확인합니다.")
	public List<CartFindResponse> findAll() {
		return cartItemService.findAll();
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "장바구니 삭제", description = "회원의 장바구니를 삭제합니다.")
	public void deleteById(@PathVariable Long id) {
		cartItemService.deleteById(id);
	}

	@PutMapping("/{id}")
	@Operation(summary = "장바구니를 수정", description = "장바구니를 수정합니다. (갯수)")
	public void updateById(@PathVariable Long id, @Valid @RequestBody UpdateCartItemRequest request) {
		cartItemService.updateById(id, request);
	}
}
