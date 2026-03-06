package kr.co.lunatalk.domain.exhibition.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import kr.co.lunatalk.domain.exhibition.domain.Exhibition
import kr.co.lunatalk.domain.exhibition.domain.ExhibitionVisibility
import kr.co.lunatalk.domain.exhibition.dto.ExhibitionProductDto
import java.time.LocalDateTime

data class ExhibitionFindOneResponse(
    @field:Schema(description = "기획전 ID")
    val exhibitionId: Long?,

    @field:Schema(description = "기획전 이름")
    val title: String,

    @field:Schema(description = "기획전 설명")
    val description: String?,

    @field:Schema(description = "기획전 노출 여부")
    val visibility: ExhibitionVisibility?,

    @field:Schema(description = "기획전 시작일")
    val startAt: LocalDateTime,

    @field:Schema(description = "기획전 종료일")
    val endAt: LocalDateTime?,

    @field:Schema(description = "연동된 상품들")
    val products: List<ExhibitionProductDto>
) {
    companion object {
        fun from(exhibition: Exhibition, products: List<ExhibitionProductDto>): ExhibitionFindOneResponse {
            return ExhibitionFindOneResponse(
                exhibitionId = exhibition.id,
                title = exhibition.title,
                description = exhibition.description,
                visibility = exhibition.visibility,
                startAt = exhibition.startAt,
                endAt = exhibition.endAt,
                products = products
            )
        }
    }
}
