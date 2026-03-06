package kr.co.lunatalk.domain.exhibition.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import kr.co.lunatalk.domain.exhibition.domain.Exhibition
import kr.co.lunatalk.domain.exhibition.dto.ExhibitionProductDto

data class ExhibitionListResponse(
    @field:Schema(description = "기획전 리스트")
    val list: List<ExhibitionFindOneResponse>
) {
    companion object {
        fun from(
            exhibitions: List<Exhibition>,
            productMap: Map<Long?, List<ExhibitionProductDto>>
        ): ExhibitionListResponse {
            val result = exhibitions.map { exhibition ->
                ExhibitionFindOneResponse.from(
                    exhibition,
                    productMap.getOrDefault(exhibition.id, emptyList())
                )
            }
            return ExhibitionListResponse(result)
        }
    }
}
