package kr.co.lunatalk.domain.cartitem.service

import kr.co.lunatalk.domain.cartitem.domain.CartItem
import kr.co.lunatalk.domain.cartitem.dto.request.CreateCartItemRequest
import kr.co.lunatalk.domain.cartitem.dto.request.UpdateCartItemRequest
import kr.co.lunatalk.domain.cartitem.dto.response.CartFindResponse
import kr.co.lunatalk.domain.cartitem.dto.response.CreateCartItemResponse
import kr.co.lunatalk.domain.cartitem.repository.CartItemRepository
import kr.co.lunatalk.domain.product.dto.FindProductDto
import kr.co.lunatalk.domain.productlike.service.ProductLikeService
import kr.co.lunatalk.global.exception.CustomException
import kr.co.lunatalk.global.exception.ErrorCode
import kr.co.lunatalk.global.util.MemberUtil
import kr.co.lunatalk.global.util.ProductUtil
import kr.co.lunatalk.global.util.SecurityUtil
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CartItemService(
    private val cartItemRepository: CartItemRepository,
    private val memberUtil: MemberUtil,
    private val productUtil: ProductUtil,
    private val productLikeService: ProductLikeService,
    private val securityUtil: SecurityUtil
) {

    fun create(request: CreateCartItemRequest): CreateCartItemResponse {
        val member = memberUtil.currentMember
        val product = productUtil.findProductId(request.productId!!)

        val cartItem = CartItem.createCartItem(member, product, request.quantity)

        cartItemRepository.save(cartItem)

        return CreateCartItemResponse.from(cartItem)
    }

    fun findAll(): List<CartFindResponse> {
        val member = memberUtil.currentMember

        val cartItems = cartItemRepository.findByMemberId(member.id!!)

        val productIds = cartItems.map { it.product!!.id!! }

        val productWithImagesResult = productUtil.findAllProducts(productIds)

        val products = productWithImagesResult.products
        val imageMap = productWithImagesResult.imageMap

        val likeCountMap = productLikeService.getLikeCounts(productIds)
        val memberId = getCurrentMemberId()
        val likedStatusMap = productLikeService.getLikedStatus(productIds, memberId)

        return cartItems.mapNotNull { cartItem ->
            val findProduct = products.firstOrNull { p ->
                p.id == cartItem.product!!.id
            } ?: return@mapNotNull null

            val likeCount = likeCountMap.getOrDefault(findProduct.id!!, 0L)
            val isLiked = likedStatusMap.getOrDefault(findProduct.id!!, false)
            CartFindResponse.of(
                cartItem,
                FindProductDto.from(findProduct, imageMap.getOrDefault(findProduct.id!!, listOf()), likeCount, isLiked)
            )
        }
    }

    fun deleteById(id: Long) {
        val cartItem = getMyCartItem(id)
        cartItemRepository.deleteById(cartItem.id!!)
    }

    fun updateById(id: Long, request: UpdateCartItemRequest) {
        val cartItem = getMyCartItem(id)
        cartItem.updateQuantity(request.quantity!!)
    }

    private fun getMyCartItem(id: Long): CartItem {
        val member = memberUtil.currentMember

        val cartItem = cartItemRepository.findById(id).orElseThrow {
            CustomException(ErrorCode.CART_ITEM_NOT_FOUND)
        }

        val cartItemMember = cartItem.member!!

        if (member.id != cartItemMember.id) {
            throw CustomException(ErrorCode.CART_ITEM_NOT_FOUND)
        }
        return cartItem
    }

    private fun getCurrentMemberId(): Long? {
        return try {
            securityUtil.getCurrentMemberId()
        } catch (e: Exception) {
            null // 비회원인 경우
        }
    }
}
