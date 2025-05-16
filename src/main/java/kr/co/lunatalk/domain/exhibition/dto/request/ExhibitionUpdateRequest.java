package kr.co.lunatalk.domain.exhibition.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import kr.co.lunatalk.domain.exhibition.domain.ExhibitionVisibility;

import java.time.LocalDateTime;
import java.util.List;

public record ExhibitionUpdateRequest(
	@Schema(description = "기획전 이름")
	@NotNull(message = "기획전 이름을 작성해주세요.")
	String title,
	@Schema(description = "기획전 설명")
	String description,

	@Schema(description = "기획전 노출 유무")
	@NotNull(message = "노출 여부를 선택해주세요.")
	ExhibitionVisibility visibility,

	@Schema(description = "기획전 노출 상품 ID")
	@Size(min = 1)
	List<Long> productIds,

	@Schema(description = "시작일")
	@NotNull(message = "시작일")
	LocalDateTime startAt,

	@Schema(description = "종료일")
	LocalDateTime endAt
) {
	@AssertTrue(message = "종료일은 시작일 이후여야 합니다.")
	public boolean isValidDateRange() {
		if (startAt == null || endAt == null) {
			return true; // Let @NotNull handle this
		}
		return endAt.isAfter(startAt);
	}
}
