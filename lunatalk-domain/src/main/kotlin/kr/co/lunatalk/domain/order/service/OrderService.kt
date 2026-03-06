package kr.co.lunatalk.domain.order.service

import kr.co.lunatalk.domain.cartitem.service.CartItemService
import kr.co.lunatalk.domain.delivery.domain.Delivery
import kr.co.lunatalk.domain.delivery.dto.response.DeliveryFindResponse
import kr.co.lunatalk.domain.delivery.repository.DeliveryRepository
import kr.co.lunatalk.domain.image.domain.Image
import kr.co.lunatalk.domain.image.domain.ImageType
import kr.co.lunatalk.domain.image.repository.ImageRepository
import kr.co.lunatalk.domain.member.domain.Member
import kr.co.lunatalk.domain.member.domain.MemberRole
import kr.co.lunatalk.domain.member.dto.response.MemberInfoResponse
import kr.co.lunatalk.domain.order.domain.OptionSnapshot
import kr.co.lunatalk.domain.order.domain.Order
import kr.co.lunatalk.domain.order.domain.OrderItem
import kr.co.lunatalk.domain.order.domain.OrderStatus
import kr.co.lunatalk.domain.order.dto.request.OrderCreateDeliveryRequest
import kr.co.lunatalk.domain.order.dto.request.OrderCreateRequest
import kr.co.lunatalk.domain.order.dto.request.OrderUpdateRequest
import kr.co.lunatalk.domain.order.dto.response.OrderCreateResponse
import kr.co.lunatalk.domain.order.dto.response.OrderFindResponse
import kr.co.lunatalk.domain.order.dto.response.OrderItemResponse
import kr.co.lunatalk.domain.order.dto.response.OrderListResponse
import kr.co.lunatalk.domain.order.repository.OrderRepository
import kr.co.lunatalk.domain.product.repository.ProductRepository
import kr.co.lunatalk.global.exception.CustomException
import kr.co.lunatalk.global.exception.ErrorCode
import kr.co.lunatalk.global.util.MemberUtil
import kr.co.lunatalk.global.util.OrderUtil
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class OrderService(
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository,
    private val imageRepository: ImageRepository,
    private val deliveryRepository: DeliveryRepository,
    private val cartItemService: CartItemService,
    private val orderUtil: OrderUtil,
    private val memberUtil: MemberUtil
) {

    fun createOrder(request: OrderCreateRequest): OrderCreateResponse {
        val member = memberUtil.currentMember
        val orderNumber = orderUtil.generateOrderNumber()

        val order = Order.createOrder(orderNumber, member, 0L)

        var totalPrice = 0L

        for (p in request.products!!) {
            val product = productRepository.findById(p.productId!!)
                .orElseThrow { CustomException(ErrorCode.PRODUCT_NOT_FOUND) }

            if (product.quantity != null && product.quantity!! <= 0) {
                cartItemService.deleteCartItemByMemberIdAndProductId(member.id!!, p.productId)
                throw CustomException(ErrorCode.PRODUCT_SOLD_OUT)
            }

            val price = product.price!!
            val quantity = p.quantity
            val itemTotal = price * quantity

            val color = if (p.optionSnapshot?.color.isNullOrEmpty()) "DEFAULT" else p.optionSnapshot.color!!
            val optionSnapshot = OptionSnapshot.createOptionSnapshot(color)

            val orderItem = OrderItem.createOrderItem(
                order,
                p.productId,
                product.name,
                price,
                quantity,
                itemTotal,
                optionSnapshot
            )

            order.addOrderItem(orderItem)
            totalPrice += itemTotal
        }

        order.updateTotalPrice(totalPrice)

        orderRepository.save(order)

        return OrderCreateResponse.of(order.orderNumber!!, order.id!!)
    }

    @Transactional(readOnly = true)
    fun findOrder(orderNumber: String): OrderFindResponse {
        val findOrder = findOrderWithOrderItemsByOrderNumber(orderNumber)

        val isMyOrder = isMyOrder(findOrder)

        if (!isMyOrder) {
            throw CustomException(ErrorCode.ORDER_NOT_FOUND)
        }

        return toOrderFindResponseWithProductImages(findOrder)
    }

    @Transactional(readOnly = true)
    fun findOrdersByMemberId(memberId: Long, pageable: Pageable): Page<OrderFindResponse> {
        val orders = orderRepository.findOrdersWithItemsByMemberId(memberId, pageable)
        return orders.map { OrderFindResponse.from(it) }
    }

    @Transactional(readOnly = true)
    fun findOrders(
        orderNumber: String?,
        orderStatus: OrderStatus?,
        username: String?,
        email: String?,
        nickname: String?,
        phone: String?,
        pageable: Pageable
    ): Page<OrderListResponse> {
        val orders = orderRepository.findOrders(orderNumber, orderStatus, username, email, nickname, phone, pageable)
        return orders.map { OrderListResponse.from(it) }
    }

    fun createDelivery(orderNumber: String, request: OrderCreateDeliveryRequest) {
        val findOrder = findOrderWithOrderItemsByOrderNumber(orderNumber)

        val isMyOrder = isMyOrder(findOrder)
        if (!isMyOrder) {
            throw CustomException(ErrorCode.ORDER_NOT_FOUND)
        }

        val delivery = Delivery.createDelivery(
            findOrder,
            request.name!!,
            request.phoneNumber!!,
            request.address1!!,
            request.address2!!,
            request.zipCode!!,
            request.message
        )

        deliveryRepository.save(delivery)
    }

    fun updateOrder(orderNumber: String, request: OrderUpdateRequest) {
        val order = findOrderWithOrderItemsByOrderNumber(orderNumber)
        order.updateStatus(request.status)
    }

    private fun findOrderWithOrderItemsByOrderNumber(orderNumber: String): Order {
        return orderRepository.findByOrderWithItems(orderNumber).orElseThrow {
            CustomException(ErrorCode.ORDER_NOT_FOUND)
        }
    }

    private fun toOrderFindResponseWithProductImages(order: Order): OrderFindResponse {
        val productIds = order.orderItems
            .mapNotNull { it.productId }
            .distinct()

        val productIdToThumbnailPath = buildProductThumbnailMap(productIds)

        val orderItems = order.orderItems.map { item ->
            OrderItemResponse.from(item, productIdToThumbnailPath[item.productId])
        }

        return OrderFindResponse(
            orderId = order.id,
            orderNumber = order.orderNumber,
            status = order.status?.value,
            totalPrice = order.totalPrice,
            orderItems = orderItems,
            deliveries = order.deliverys.map { DeliveryFindResponse.from(it) },
            member = order.member?.let { MemberInfoResponse.from(it) },
            createdAt = order.createdAt
        )
    }

    private fun buildProductThumbnailMap(productIds: List<Long>): Map<Long, String?> {
        if (productIds.isEmpty()) {
            return emptyMap()
        }

        val images = imageRepository.fetchProductImagesByProductIds(productIds)

        val imageMap = images.filter { it.referenceId != null }.groupBy { it.referenceId!! }

        return imageMap.mapValues { (_, value) -> pickThumbnailPath(value) }
    }

    private fun pickThumbnailPath(images: List<Image>?): String? {
        if (images.isNullOrEmpty()) {
            return null
        }

        return images
            .sortedWith(
                compareBy<Image> { if (it.imageType == ImageType.PRODUCT_THUMBNAIL) 0 else 1 }
                    .thenBy(nullsLast()) { it.imageOrder }
            )
            .firstOrNull()
            ?.imagePath
    }

    private fun isMyOrder(order: Order): Boolean {
        val currentMember = memberUtil.currentMember
        val isAdmin = currentMember.role == MemberRole.ADMIN
        return isAdmin || order.member?.id == currentMember.id
    }
}
