package kr.co.lunatalk.domain.exhibition.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import kr.co.lunatalk.domain.exhibition.domain.Exhibition

data class ExhibitionCreateResponse(
    @field:Schema(description = "생성한 exhibitionId")
    val exhibitionId: Long?
) {
    companion object {
        fun from(exhibition: Exhibition): ExhibitionCreateResponse {
            return ExhibitionCreateResponse(exhibition.id)
        }
    }
}
