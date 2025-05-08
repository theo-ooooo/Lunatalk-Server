package kr.co.lunatalk.domain.delivery.repository;

import kr.co.lunatalk.domain.delivery.domain.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRepository extends JpaRepository<Delivery, Long>, DeliveryRepositoryCustom {
}
