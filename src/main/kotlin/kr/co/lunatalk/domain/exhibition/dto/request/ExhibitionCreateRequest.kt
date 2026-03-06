package kr.co.lunatalk.domain.exhibition.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.AssertTrue
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import kr.co.lunatalk.domain.exhibition.domain.ExhibitionVisibility
import java.time.LocalDateTime

data class ExhibitionCreateRequest(
    @field:Schema(description = "기획전 이름")
    @field:NotNull(message = "기획전 이름을 작성해주세요.")
    val title: String? = null,

    @field:Schema(description = "기획전 설명")
    val description: String? = null,

    @field:Schema(description = "기획전 노출 유무")
    @field:NotNull(message = "노출 여부를 선택해주세요.")
    val visibility: ExhibitionVisibility? = null,

    @field:Schema(description = "기획전 노출 상품 ID")
    @field:Size(min = 1)
    val productIds: List<Long>? = null,

    @field:Schema(description = "시작일")
    @field:NotNull(message = "시작일")
    val startAt: LocalDateTime? = null,

    @field:Schema(description = "종료일")
    val endAt: LocalDateTime? = null
) {
    @AssertTrue(message = "종료일은 시작일 이후여야 합니다.")
    fun isValidDateRange(): Boolean {
        if (startAt == null || endAt == null) {
            return true // Let @NotNull handle this
        }
        return endAt.isAfter(startAt)
    }
}
