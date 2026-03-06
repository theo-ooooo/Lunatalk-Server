package kr.co.lunatalk.domain.payment.service

import kr.co.lunatalk.domain.cartitem.repository.CartItemRepository
import kr.co.lunatalk.domain.order.domain.OrderStatus
import kr.co.lunatalk.domain.order.repository.OrderRepository
import kr.co.lunatalk.domain.payment.domain.Payment
import kr.co.lunatalk.domain.payment.dto.request.PaymentCancelRequest
import kr.co.lunatalk.domain.payment.dto.request.PaymentConfirmRequest
import kr.co.lunatalk.domain.payment.dto.response.PaymentCancelResponse
import kr.co.lunatalk.domain.payment.dto.response.PaymentConfirmResponse
import kr.co.lunatalk.domain.payment.dto.toss.TossPaymentConfirmRequest
import kr.co.lunatalk.domain.payment.dto.toss.TossPaymentConfirmResponse
import kr.co.lunatalk.domain.payment.event.PaymentCompletedEvent
import kr.co.lunatalk.domain.payment.repository.PaymentRepository
import kr.co.lunatalk.domain.product.repository.ProductRepository
import kr.co.lunatalk.global.exception.CustomException
import kr.co.lunatalk.global.exception.ErrorCode
import kr.co.lunatalk.global.util.MemberUtil
import kr.co.lunatalk.infra.config.toss.TossPaymentsProperties
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientException

@Service
@Transactional
class PaymentService(
    private val orderRepository: OrderRepository,
    private val paymentRepository: PaymentRepository,
    private val cartItemRepository: CartItemRepository,
    private val productRepository: ProductRepository,
    private val tossPaymentsProperties: TossPaymentsProperties,
    private val tossPaymentsRestClient: RestClient,
    private val memberUtil: MemberUtil,
    private val applicationEventPublisher: ApplicationEventPublisher
) {

    fun confirmPayment(request: PaymentConfirmRequest): PaymentConfirmResponse {
        val member = memberUtil.currentMember

        val order = orderRepository.findByOrderWithItems(request.orderId)
            .orElseThrow { CustomException(ErrorCode.ORDER_NOT_FOUND) }

        if (member.id != order.member?.id) {
            throw CustomException(ErrorCode.ORDER_NOT_FOUND)
        }

        if (order.totalPrice != request.amount) {
            throw CustomException(ErrorCode.PAYMENT_AMOUNT_MISMATCH)
        }

        val tossRequest = TossPaymentConfirmRequest(
            paymentKey = request.paymentKey,
            orderId = request.orderId,
            amount = request.amount
        )

        val tossResponse: TossPaymentConfirmResponse = try {
            tossPaymentsRestClient.post()
                .uri("/v1/payments/confirm")
                .headers { headers -> headers.setBasicAuth(tossPaymentsProperties.secretKey, "") }
                .body(tossRequest)
                .retrieve()
                .body(TossPaymentConfirmResponse::class.java)
                ?: throw CustomException(ErrorCode.PAYMENT_CONFIRM_FAILED)
        } catch (e: RestClientException) {
            throw CustomException(ErrorCode.PAYMENT_CONFIRM_FAILED)
        }

        val payment = Payment.success(
            order = order,
            paymentKey = tossResponse.paymentKey!!,
            orderNumber = tossResponse.orderId!!,
            amount = tossResponse.totalAmount!!,
            method = tossResponse.method,
            approvedAt = tossResponse.getApprovedAtAsLocalDateTime()
        )

        paymentRepository.save(payment)

        for (orderItem in order.orderItems) {
            val productId = orderItem.productId!!
            val product = productRepository.findById(productId)
                .orElseThrow { CustomException(ErrorCode.PRODUCT_NOT_FOUND) }

            if (orderItem.quantity!! > product.quantity!!) {
                throw CustomException(ErrorCode.PRODUCT_SOLD_OUT)
            }
            product.minusProductQuantity(orderItem.quantity!!)
            cartItemRepository.deleteByMemberIdAndProductId(member.id!!, productId)
        }

        order.updateStatus(OrderStatus.PAYMENT_COMPLETED)

        publishPaymentCompletedEvent(order)

        return PaymentConfirmResponse.of(order, payment)
    }

    fun cancelPayment(request: PaymentCancelRequest): PaymentCancelResponse {
        val payment = paymentRepository.findByOrder_OrderNumber(request.orderId)
            .orElseThrow { CustomException(ErrorCode.PAYMENT_NOT_FOUND) }

        val order = payment.order!!

        try {
            tossPaymentsRestClient.post()
                .uri("/v1/payments/{paymentKey}/cancel", payment.paymentKey)
                .headers { headers -> headers.setBasicAuth(tossPaymentsProperties.secretKey, "") }
                .body(mapOf("cancelReason" to request.cancelReason))
                .retrieve()
                .toBodilessEntity()
        } catch (e: RestClientException) {
            throw CustomException(ErrorCode.PAYMENT_CANCEL_FAILED)
        }

        payment.cancel()
        order.updateStatus(OrderStatus.CANCELLED)

        return PaymentCancelResponse.of(order, payment)
    }

    private fun publishPaymentCompletedEvent(order: kr.co.lunatalk.domain.order.domain.Order) {
        val items = order.orderItems.map { i ->
            PaymentCompletedEvent.PaymentOrderItem(
                productId = i.productId!!,
                productName = i.productName!!,
                quantity = i.quantity!!,
                price = i.price!!
            )
        }

        applicationEventPublisher.publishEvent(
            PaymentCompletedEvent(
                orderNumber = order.orderNumber!!,
                orderId = order.id!!,
                totalAmount = order.totalPrice!!,
                memberEmail = order.member!!.email ?: "",
                items = items
            )
        )
    }
}
