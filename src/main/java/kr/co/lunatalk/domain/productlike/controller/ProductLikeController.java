package kr.co.lunatalk.domain.productlike.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.lunatalk.domain.productlike.service.ProductLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products/{productId}/likes")
@RequiredArgsConstructor
@Tag(name = "상품 좋아요", description = "상품 좋아요 관련 API")
public class ProductLikeController {

	private final ProductLikeService productLikeService;

	@PostMapping
	@Operation(summary = "상품 좋아요 토글", description = "상품에 좋아요를 누르거나 취소합니다. (회원만 가능)")
	public ResponseEntity<Void> toggleLike(@PathVariable Long productId) {
		productLikeService.toggleLike(productId);
		return ResponseEntity.ok().build();
	}
}

