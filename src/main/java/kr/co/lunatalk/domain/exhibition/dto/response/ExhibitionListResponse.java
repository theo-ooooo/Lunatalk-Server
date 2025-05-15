package kr.co.lunatalk.domain.exhibition.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.lunatalk.domain.exhibition.domain.Exhibition;
import kr.co.lunatalk.domain.exhibition.dto.ExhibitionProductDto;

import java.util.List;
import java.util.Map;

public record ExhibitionListResponse(
	@Schema(description = "기획전 리스트")
	List<ExhibitionFindOneResponse> list
) {

	public static ExhibitionListResponse from(List<Exhibition> exhibitions, Map<Long, List<ExhibitionProductDto>> productMap) {
		List<ExhibitionFindOneResponse> result = exhibitions.stream()
			.map(exhibition -> ExhibitionFindOneResponse.from(
				exhibition,
				productMap.getOrDefault(exhibition.getId(), List.of()) // 기획전에 연결된 상품들
			))
			.toList();

		return new ExhibitionListResponse(result);
	}

}
