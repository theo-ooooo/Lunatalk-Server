package kr.co.lunatalk.domain.productlike.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kr.co.lunatalk.domain.product.dto.response.ProductFindResponse
import kr.co.lunatalk.domain.productlike.service.ProductLikeService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/products/likes")
@Tag(name = "상품 좋아요", description = "상품 좋아요 관련 API")
class ProductLikeQueryController(
    private val productLikeService: ProductLikeService
) {

    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @Operation(summary = "내가 좋아요한 상품 목록 조회", description = "로그인한 사용자가 좋아요한 상품 목록을 페이징 조회합니다.")
    fun getMyLikedProducts(
        @PageableDefault(size = 10, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable
    ): Page<ProductFindResponse> {
        return productLikeService.findMyLikedProducts(pageable)
    }
}
