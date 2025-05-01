package kr.co.lunatalk.domain.image.dto.response;

public record PresignedUrlResponse(String presignedUrl, String imageKey) {

	public static PresignedUrlResponse of(String presignedUrl, String imageKey) {
		return new PresignedUrlResponse(presignedUrl, imageKey);
	}
}
