package kr.co.lunatalk.domain.delivery.domain

import jakarta.persistence.*
import kr.co.lunatalk.domain.common.domain.BaseTimeEntity
import kr.co.lunatalk.domain.order.domain.Order

@Entity
open class Delivery protected constructor() : BaseTimeEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null
        protected set

    open var receiverName: String? = null
        protected set

    open var receiverPhone: String? = null
        protected set

    open var addressLine1: String? = null
        protected set

    open var addressLine2: String? = null
        protected set

    open var zipcode: String? = null
        protected set

    open var message: String? = null
        protected set

    open var trackingNumber: String? = null
        protected set

    @Enumerated(EnumType.STRING)
    open var courierCompany: CourierCompany? = null
        protected set

    @Enumerated(EnumType.STRING)
    open var status: DeliveryStatus? = null
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    open var order: Order? = null
        protected set

    private constructor(
        order: Order?,
        receiverName: String?,
        receiverPhone: String?,
        addressLine1: String?,
        addressLine2: String?,
        zipcode: String?,
        message: String?,
        status: DeliveryStatus?
    ) : this() {
        this.order = order
        this.receiverName = receiverName
        this.receiverPhone = receiverPhone
        this.addressLine1 = addressLine1
        this.addressLine2 = addressLine2
        this.zipcode = zipcode
        this.message = message
        this.status = status
    }

    fun updateStatus(status: DeliveryStatus) {
        this.status = status
    }

    fun updateTrackingNumber(trackingNumber: String?) {
        this.trackingNumber = trackingNumber
    }

    fun updateCourierCompany(courierCompany: CourierCompany?) {
        this.courierCompany = courierCompany
    }

    companion object {
        fun createDelivery(
            order: Order,
            receiverName: String,
            receiverPhone: String,
            addressLine1: String,
            addressLine2: String,
            zipcode: String,
            message: String?
        ): Delivery {
            return Delivery(
                order = order,
                receiverName = receiverName,
                receiverPhone = receiverPhone,
                addressLine1 = addressLine1,
                addressLine2 = addressLine2,
                zipcode = zipcode,
                message = message,
                status = DeliveryStatus.READY
            )
        }
    }
}
