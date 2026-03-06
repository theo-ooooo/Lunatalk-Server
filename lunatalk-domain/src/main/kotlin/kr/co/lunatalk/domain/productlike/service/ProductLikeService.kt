package kr.co.lunatalk.domain.productlike.service

import kr.co.lunatalk.domain.product.domain.Product
import kr.co.lunatalk.domain.product.dto.FindProductDto
import kr.co.lunatalk.domain.product.dto.response.ProductFindResponse
import kr.co.lunatalk.domain.productlike.domain.ProductLike
import kr.co.lunatalk.domain.productlike.repository.ProductLikeRepository
import kr.co.lunatalk.global.util.MemberUtil
import kr.co.lunatalk.global.util.ProductUtil
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ProductLikeService(
    private val productLikeRepository: ProductLikeRepository,
    private val memberUtil: MemberUtil,
    private val productUtil: ProductUtil
) {

    fun toggleLike(productId: Long) {
        val member = memberUtil.currentMember
        val product = productUtil.findProductId(productId)

        val existingLike = productLikeRepository.findByMemberIdAndProductId(
            member.id!!, productId
        )

        if (existingLike.isPresent) {
            productLikeRepository.delete(existingLike.get())
        } else {
            val productLike = ProductLike.create(member, product)
            productLikeRepository.save(productLike)
        }
    }

    @Transactional(readOnly = true)
    fun getLikeCount(productId: Long): Long {
        val count = productLikeRepository.countByProductId(productId)
        return count ?: 0L
    }

    @Transactional(readOnly = true)
    fun getLikeCounts(productIds: List<Long>): Map<Long, Long> {
        return productLikeRepository.countByProductIds(productIds)
    }

    @Transactional(readOnly = true)
    fun isLiked(productId: Long, memberId: Long?): Boolean {
        if (memberId == null) {
            return false
        }
        return productLikeRepository.findByMemberIdAndProductId(memberId, productId).isPresent
    }

    @Transactional(readOnly = true)
    fun getLikedStatus(productIds: List<Long>, memberId: Long?): Map<Long, Boolean> {
        if (memberId == null) {
            return productIds.associateWith { false }
        }
        return productLikeRepository.existsByMemberIdAndProductIds(memberId, productIds)
    }

    @Transactional(readOnly = true)
    fun findMyLikedProducts(pageable: Pageable): Page<ProductFindResponse> {
        val member = memberUtil.currentMember

        val likedProductIdPage = productLikeRepository.findLikedProductIdsByMemberId(member.id!!, pageable)
        val productIds = likedProductIdPage.content

        if (productIds.isEmpty()) {
            return PageImpl(emptyList(), pageable, likedProductIdPage.totalElements)
        }

        val productsWithImages = productUtil.findAllProducts(productIds)
        val likeCountMap = getLikeCounts(productIds)

        val productMap = productsWithImages.products.associateBy(
            { it.id },
            { it }
        )

        val content = productIds.mapNotNull { productId ->
            val product = productMap[productId] ?: return@mapNotNull null // 비노출/삭제 등으로 조회되지 않는 경우
            val images = productsWithImages.imageMap.getOrDefault(productId, emptyList())
            val likeCount = likeCountMap.getOrDefault(productId, 0L)
            ProductFindResponse.from(
                FindProductDto.from(product, images, likeCount, true)
            )
        }

        return PageImpl(content, pageable, likedProductIdPage.totalElements)
    }
}
