package kr.co.lunatalk.domain.product.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import kr.co.lunatalk.domain.product.dto.request.ProductCreateRequest
import kr.co.lunatalk.domain.product.dto.request.ProductUpdateRequest
import kr.co.lunatalk.domain.product.dto.response.ProductCreateResponse
import kr.co.lunatalk.domain.product.dto.response.ProductFindResponse
import kr.co.lunatalk.domain.product.service.ProductService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/products")
@Tag(name = "상품", description = "상품 관련 API")
class ProductController(
    private val productService: ProductService
) {

    @GetMapping("{id}")
    @Operation(summary = "상품 상세 조회", description = "상품 상세 조회 합니다.")
    fun getProduct(@PathVariable id: Long): ProductFindResponse {
        return productService.findProductOne(id)
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "상품 생성", description = "상품을 생성합니다.")
    fun create(@Valid @RequestBody request: ProductCreateRequest): ProductCreateResponse {
        val product = productService.save(request)
        return ProductCreateResponse.from(product)
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "상품 수정", description = "상품을 수정합니다.")
    fun update(@PathVariable id: Long, @Valid @RequestBody request: ProductUpdateRequest): ResponseEntity<Void> {
        productService.update(id, request)
        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "상품 삭제", description = "상품을 삭제 합니다.")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        productService.delete(id)
        return ResponseEntity.ok().build()
    }

    @GetMapping
    @Operation(summary = "상품 리스트", description = "전체 상품을 조회합니다.")
    fun findAll(
        @RequestParam(required = false) productName: String?,
        @PageableDefault(size = 10, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable
    ): Page<ProductFindResponse> {
        return productService.findAll(productName, pageable)
    }
}
