package kr.co.lunatalk.domain.image.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import kr.co.lunatalk.domain.image.domain.ImageFileExtension
import kr.co.lunatalk.domain.image.domain.ImageType

data class ProductImageUploadRequest(
    @field:NotNull(message = "상품 ID는 필수 값입니다.")
    @Schema(description = "상품 ID")
    val productId: Long,

    @field:NotNull(message = "이미지 타입을 선택해주세요.")
    @Schema(description = "이미지 타입")
    val imageType: ImageType,

    @field:NotNull(message = "이미지 확장자를 선택해주세요.")
    @Schema(description = "이미지 확장자")
    val imageFileExtension: ImageFileExtension
)
