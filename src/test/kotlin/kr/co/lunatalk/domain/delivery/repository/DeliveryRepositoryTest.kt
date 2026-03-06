package kr.co.lunatalk.domain.delivery.repository

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import kr.co.lunatalk.domain.delivery.domain.CourierCompany
import kr.co.lunatalk.domain.delivery.domain.Delivery
import kr.co.lunatalk.domain.delivery.domain.DeliveryStatus
import kr.co.lunatalk.domain.member.domain.Member
import kr.co.lunatalk.domain.member.domain.Profile
import kr.co.lunatalk.domain.member.repository.MemberRepository
import kr.co.lunatalk.domain.order.domain.Order
import kr.co.lunatalk.domain.order.repository.OrderRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class DeliveryRepositoryTest {

    @Autowired
    private lateinit var deliveryRepository: DeliveryRepository

    @Autowired
    private lateinit var orderRepository: OrderRepository

    @Autowired
    private lateinit var memberRepository: MemberRepository

    @PersistenceContext
    private lateinit var em: EntityManager

    private lateinit var testOrder: Order

    @BeforeEach
    fun setUp() {
        val member = Member.createMember(
            "test", "1234", Profile.of("test", "test"),
            "01012341234", "kkwondev@gmail.com"
        )
        memberRepository.save(member)

        testOrder = Order.createOrder("abcdef", member, 10L)
        orderRepository.save(testOrder)
    }

    @Test
    fun `배송 정보 저장`() {
        val delivery = Delivery.createDelivery(
            testOrder, "강경원", "01012341234", "서울", "강남구", "10000", "테스트 배송"
        )
        deliveryRepository.save(delivery)
        em.flush()
        em.clear()

        val order = orderRepository.findById(testOrder.id).get()

        assertThat(delivery.id).isNotNull()
        assertThat(order.deliverys)
            .extracting("id")
            .contains(delivery.id)
    }

    @Test
    fun `배송 상태 변경`() {
        val delivery = Delivery.createDelivery(
            testOrder, "홍길동", "01000000000", "서울", "강북구", "11111", "비고"
        )
        deliveryRepository.save(delivery)

        delivery.updateStatus(DeliveryStatus.SHIPPED)
        deliveryRepository.flush()

        val found = deliveryRepository.findById(delivery.id).orElseThrow()
        assertThat(found.status).isEqualTo(DeliveryStatus.SHIPPED)
    }

    @Test
    fun `운송장번호 택배사 정보 변경`() {
        val delivery = Delivery.createDelivery(
            testOrder, "홍길동", "01000000000", "서울", "강북구", "11111", "비고"
        )
        deliveryRepository.save(delivery)

        delivery.updateTrackingNumber("TRACK123456")
        delivery.updateCourierCompany(CourierCompany.CJ_LOGISTICS)
        deliveryRepository.flush()

        val found = deliveryRepository.findById(delivery.id).orElseThrow()
        assertThat(found.trackingNumber).isEqualTo("TRACK123456")
        assertThat(found.courierCompany).isEqualTo(CourierCompany.CJ_LOGISTICS)
    }
}
