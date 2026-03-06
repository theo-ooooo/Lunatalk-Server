package kr.co.lunatalk.domain.delivery.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import kr.co.lunatalk.domain.delivery.domain.CourierCompany
import kr.co.lunatalk.domain.delivery.domain.Delivery
import kr.co.lunatalk.domain.delivery.domain.DeliveryStatus

data class DeliveryFindResponse(
    @Schema(description = "배송 고유 ID")
    val deliveryId: Long?,

    @Schema(description = "수취인 이름")
    val receiverName: String?,

    @Schema(description = "수취인 휴대폰 번호")
    val receiverPhone: String?,

    @Schema(description = "주소1")
    val addressLine1: String?,

    @Schema(description = "주소2")
    val addressLine2: String?,

    @Schema(description = "우편번호")
    val zipcode: String?,

    @Schema(description = "배송 메세지")
    val message: String?,

    @Schema(description = "택배 회사")
    val courierCompany: CourierCompany?,

    @Schema(description = "운송장 번호")
    val trackingNumber: String?,

    @Schema(description = "배송 상태")
    val status: DeliveryStatus?
) {
    companion object {
        fun from(delivery: Delivery): DeliveryFindResponse {
            return DeliveryFindResponse(
                deliveryId = delivery.id,
                receiverName = delivery.receiverName,
                receiverPhone = delivery.receiverPhone,
                addressLine1 = delivery.addressLine1,
                addressLine2 = delivery.addressLine2,
                zipcode = delivery.zipcode,
                message = delivery.message,
                courierCompany = delivery.courierCompany,
                trackingNumber = delivery.trackingNumber,
                status = delivery.status
            )
        }
    }
}
