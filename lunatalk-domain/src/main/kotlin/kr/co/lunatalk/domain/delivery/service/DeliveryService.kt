package kr.co.lunatalk.domain.delivery.service

import kr.co.lunatalk.domain.delivery.dto.request.DeliveryUpdateRequest
import kr.co.lunatalk.domain.delivery.repository.DeliveryRepository
import kr.co.lunatalk.global.exception.CustomException
import kr.co.lunatalk.global.exception.ErrorCode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DeliveryService(
    private val deliveryRepository: DeliveryRepository
) {

    fun update(deliveryId: Long, request: DeliveryUpdateRequest) {
        val delivery = deliveryRepository.findById(deliveryId)
            .orElseThrow { CustomException(ErrorCode.DELIVERY_NOT_FOUND) }

        request.courierCompany?.let { delivery.updateCourierCompany(it) }
        request.trackingNumber?.let { delivery.updateTrackingNumber(it) }
        request.status?.let { delivery.updateStatus(it) }
    }
}
