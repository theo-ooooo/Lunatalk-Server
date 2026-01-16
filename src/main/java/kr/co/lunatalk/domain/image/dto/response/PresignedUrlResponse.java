package kr.co.lunatalk.domain.image.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record PresignedUrlResponse(
	@Schema(description = "이미지 업로드 presigned url")
	String presignedUrl,
	@Schema(description = "이미지 키")
	String imageKey
) {

	public static PresignedUrlResponse of(String presignedUrl, String imageKey) {
		return new PresignedUrlResponse(presignedUrl, imageKey);
	}
}
