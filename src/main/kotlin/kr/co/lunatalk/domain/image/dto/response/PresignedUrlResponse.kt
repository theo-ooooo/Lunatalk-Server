package kr.co.lunatalk.domain.image.dto.response

import io.swagger.v3.oas.annotations.media.Schema

data class PresignedUrlResponse(
    @Schema(description = "이미지 업로드 presigned url")
    val presignedUrl: String,

    @Schema(description = "이미지 키")
    val imageKey: String
) {
    companion object {
        fun of(presignedUrl: String, imageKey: String): PresignedUrlResponse {
            return PresignedUrlResponse(presignedUrl, imageKey)
        }
    }
}
