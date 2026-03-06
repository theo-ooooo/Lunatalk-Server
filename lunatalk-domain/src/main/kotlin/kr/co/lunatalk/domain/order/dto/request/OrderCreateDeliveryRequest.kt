package kr.co.lunatalk.domain.order.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull

data class OrderCreateDeliveryRequest(
    @field:NotNull(message = "주소를 입력해주세요.")
    @Schema(description = "주소1")
    val address1: String?,

    @field:NotNull(message = "주소를 입력해주세요.")
    @Schema(description = "주소2")
    val address2: String?,

    @field:NotNull(message = "핸드폰 번호를 입력해주세요.")
    @Schema(description = "핸드폰 번호")
    val phoneNumber: String?,

    @field:NotNull(message = "우편번호를 입력해주세요.")
    @Schema(description = "우편번호")
    val zipCode: String?,

    @field:NotNull(message = "이름을 입력해주세요.")
    @Schema(description = "이름")
    val name: String?,

    @Schema(description = "배송 메세지")
    val message: String? = null
)
