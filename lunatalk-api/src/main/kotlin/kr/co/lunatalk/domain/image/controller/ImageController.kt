package kr.co.lunatalk.domain.image.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import kr.co.lunatalk.domain.image.dto.request.ProductImageCompletedRequest
import kr.co.lunatalk.domain.image.dto.request.ProductImageUploadRequest
import kr.co.lunatalk.domain.image.dto.response.PresignedUrlResponse
import kr.co.lunatalk.domain.image.service.ImageService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/images")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "이미지 업로드/삭제", description = "이미지 관련 API")
class ImageController(
    private val imageService: ImageService
) {

    @PostMapping("/products/upload-url")
    @Operation(summary = "이미지 Presigned url 생성", description = "presigned url를 생성합니다.")
    fun productPresignedUrlCreate(@RequestBody @Valid request: ProductImageUploadRequest): PresignedUrlResponse {
        return imageService.productImageUpload(request)
    }

    @PostMapping("/products/upload-complete")
    @Operation(summary = "생성한 presigned url 완료", description = "생성한 presigned url 완료 처리합니다.")
    fun productImageUploaded(@RequestBody @Valid request: ProductImageCompletedRequest) {
        imageService.productImageCompleteUpload(request)
    }

    @DeleteMapping("/delete/{imageKey}")
    @Operation(summary = "이미지 삭제", description = "업로드된 이미지를 삭제합니다.")
    fun deleteImage(@PathVariable imageKey: String) {
        imageService.deleteByImageKey(imageKey)
    }
}
