package kr.co.lunatalk.domain.productlike.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kr.co.lunatalk.domain.productlike.service.ProductLikeService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/products/{productId}/likes")
@Tag(name = "상품 좋아요", description = "상품 좋아요 관련 API")
class ProductLikeController(
    private val productLikeService: ProductLikeService
) {

    @PostMapping
    @Operation(summary = "상품 좋아요 토글", description = "상품에 좋아요를 누르거나 취소합니다. (회원만 가능)")
    fun toggleLike(@PathVariable productId: Long): ResponseEntity<Void> {
        productLikeService.toggleLike(productId)
        return ResponseEntity.ok().build()
    }
}
