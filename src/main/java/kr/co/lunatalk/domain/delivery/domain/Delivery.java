package kr.co.lunatalk.domain.delivery.domain;

import jakarta.persistence.*;
import kr.co.lunatalk.domain.common.domain.BaseTimeEntity;
import kr.co.lunatalk.domain.order.domain.Order;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Delivery extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;


	private String receiverName;
	private String receiverPhone;
	private String addressLine1;
	private String addressLine2;
	private String zipcode;

	private String trackingNumber;

	@Enumerated(EnumType.STRING)
	private CourierCompany courierCompany;

	@Enumerated(EnumType.STRING)
	private DeliveryStatus status;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id")
	private Order order;


	@Builder(access = AccessLevel.PRIVATE)
	public Delivery(Order order, String receiverName, String receiverPhone, String addressLine1, String addressLine2, String zipcode) {
		this.order = order;
		this.receiverName = receiverName;
		this.receiverPhone = receiverPhone;
		this.addressLine1 = addressLine1;
		this.addressLine2 = addressLine2;
		this.zipcode = zipcode;
	}

	public static Delivery createDelivery(Order order, String receiverName, String receiverPhone, String addressLine1, String addressLine2, String zipcode) {
		return Delivery.builder()
			.order(order)
			.receiverName(receiverName)
			.receiverPhone(receiverPhone)
			.addressLine1(addressLine1)
			.addressLine2(addressLine2)
			.zipcode(zipcode)
			.build();
	}

	public void updateStatus(DeliveryStatus status) {
		this.status = status;
	}

	public void updateTrackingNumber(String trackingNumber) {
		this.trackingNumber = trackingNumber;
	}
}
