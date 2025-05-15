package kr.co.lunatalk.domain.exhibition.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.lunatalk.domain.exhibition.domain.Exhibition;
import kr.co.lunatalk.domain.exhibition.domain.ExhibitionVisibility;
import kr.co.lunatalk.domain.exhibition.dto.ExhibitionProductDto;
import kr.co.lunatalk.domain.product.domain.Product;
import kr.co.lunatalk.domain.product.dto.FindProductDto;

import java.time.LocalDateTime;
import java.util.List;

public record ExhibitionFindOneResponse(
	@Schema(description = "기획전 ID")
	Long exhibitionId,
	@Schema(description = "기획전 이름")
	String title,
	@Schema(description = "기획전 설명")
	String description,

	@Schema(description = "기획전 노출 여부")
	ExhibitionVisibility visibility,

	@Schema(description = "기획전 시작일")
	LocalDateTime startAt,

	@Schema(description = "기획전 종료일")
	LocalDateTime endAt,

	@Schema(description = "연동된 상품들")
	List<ExhibitionProductDto> products
) {

	public static ExhibitionFindOneResponse from(Exhibition exhibition, List<ExhibitionProductDto> products) {
		return new ExhibitionFindOneResponse(
			exhibition.getId(),
			exhibition.getTitle(),
			exhibition.getDescription(),
			exhibition.getVisibility(),
			exhibition.getStartAt(),
			exhibition.getEndAt(),
			products
		);
	}
}
