package kr.co.lunatalk.domain.delivery.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import kr.co.lunatalk.domain.delivery.domain.CourierCompany
import kr.co.lunatalk.domain.delivery.domain.DeliveryStatus

data class DeliveryUpdateRequest(
    @Schema(description = "변경할 운송장 번호")
    val trackingNumber: String?,

    @Schema(description = "변경할 택배 회사")
    val courierCompany: CourierCompany?,

    @Schema(description = "변경할 상태")
    val status: DeliveryStatus?
)
