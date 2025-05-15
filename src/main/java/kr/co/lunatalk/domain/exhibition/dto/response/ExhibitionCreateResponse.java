package kr.co.lunatalk.domain.exhibition.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.lunatalk.domain.exhibition.domain.Exhibition;

public record ExhibitionCreateResponse(
	@Schema(description = "생성한 exhibitionId")
	Long exhibitionId
) {
	public static ExhibitionCreateResponse from(Exhibition exhibition) {
		return new ExhibitionCreateResponse(exhibition.getId());
	}
}
