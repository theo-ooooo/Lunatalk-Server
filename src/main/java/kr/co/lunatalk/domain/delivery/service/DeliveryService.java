package kr.co.lunatalk.domain.delivery.service;

import kr.co.lunatalk.domain.delivery.domain.Delivery;
import kr.co.lunatalk.domain.delivery.dto.request.DeliveryUpdateRequest;
import kr.co.lunatalk.domain.delivery.repository.DeliveryRepository;
import kr.co.lunatalk.global.exception.CustomException;
import kr.co.lunatalk.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DeliveryService {
	private final DeliveryRepository deliveryRepository;


	public void update(Long deliveryId, DeliveryUpdateRequest request) {
		Delivery delivery = findDelivery(deliveryId);

		delivery.updateCourierCompany(request.courierCompany());
		delivery.updateTrackingNumber(request.trackingNumber());
		delivery.updateStatus(request.status());
	}

	private Delivery findDelivery(Long deliveryId) {
		return deliveryRepository.findById(deliveryId).orElseThrow(() -> new CustomException(ErrorCode.DELIVERY_NOT_FOUND));
	}
}
