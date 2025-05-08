package kr.co.lunatalk.domain.delivery.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import kr.co.lunatalk.domain.delivery.domain.CourierCompany;
import kr.co.lunatalk.domain.delivery.domain.Delivery;
import kr.co.lunatalk.domain.delivery.domain.DeliveryStatus;
import kr.co.lunatalk.domain.member.domain.Member;
import kr.co.lunatalk.domain.member.domain.Profile;
import kr.co.lunatalk.domain.member.repository.MemberRepository;
import kr.co.lunatalk.domain.order.domain.Order;
import kr.co.lunatalk.domain.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class DeliveryRepositoryTest {



	@Autowired private DeliveryRepository deliveryRepository;
	@Autowired private OrderRepository orderRepository;
	@Autowired private MemberRepository memberRepository;
	@PersistenceContext
	EntityManager em;

	private Order testOrder;

	@BeforeEach
	void setUp() {
		Member member = Member.createMember("test", "1234", Profile.of("test", "test"));
		memberRepository.save(member); // ✅ 먼저 저장

		testOrder = Order.createOrder("abcdef", member, 10L);
		orderRepository.save(testOrder); // ✅ 그 다음 저장
	}

	@Test
	void 배송_정보_저장() {
		Delivery delivery = Delivery.createDelivery(testOrder, "강경원", "01012341234", "서울", "강남구", "10000", "테스트 배송");
		deliveryRepository.save(delivery);
		em.flush();
		em.clear();

		Order order = orderRepository.findById(testOrder.getId()).get();

		assertThat(delivery.getId()).isNotNull();
		assertThat(order.getDeliverys())
			.extracting("id")
			.contains(delivery.getId());
	}

	@Test
	void 배송_상태_변경() {
		Delivery delivery = Delivery.createDelivery(testOrder, "홍길동", "01000000000", "서울", "강북구", "11111", "비고");
		deliveryRepository.save(delivery);

		delivery.updateStatus(DeliveryStatus.SHIPPED);
		deliveryRepository.flush();

		Delivery found = deliveryRepository.findById(delivery.getId()).orElseThrow();
		assertThat(found.getStatus()).isEqualTo(DeliveryStatus.SHIPPED);
	}

	@Test
	void 운송장번호_택배사_정보_변경() {
		Delivery delivery = Delivery.createDelivery(testOrder, "홍길동", "01000000000", "서울", "강북구", "11111", "비고");
		deliveryRepository.save(delivery);

		delivery.updateTrackingNumber("TRACK123456");
		delivery.updateCourierCompany( CourierCompany.CJ_LOGISTICS);
		deliveryRepository.flush();

		Delivery found = deliveryRepository.findById(delivery.getId()).orElseThrow();
		assertThat(found.getTrackingNumber()).isEqualTo("TRACK123456");
		assertThat(found.getCourierCompany()).isEqualTo(CourierCompany.CJ_LOGISTICS);
	}
}
