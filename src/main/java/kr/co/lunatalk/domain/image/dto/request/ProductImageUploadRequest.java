package kr.co.lunatalk.domain.image.dto.request;

import jakarta.validation.constraints.NotNull;
import kr.co.lunatalk.domain.image.domain.ImageFileExtension;
import kr.co.lunatalk.domain.image.domain.ImageType;

public record ProductImageUploadRequest(
	@NotNull(message = "상품 ID는 필수 값입니다.")
	Long productId,
	@NotNull(message = "이미지 타입을 선택해주세요.")
	ImageType imageType,
	@NotNull(message = "이미지 확장자를 선택해주세요.")
	ImageFileExtension imageFileExtension
) {
}
