package kr.co.lunatalk.domain.productlike.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ProductLikeRepositoryCustom {

    fun countByProductId(productId: Long): Long?

    fun countByProductIds(productIds: List<Long>): Map<Long, Long>

    fun existsByMemberIdAndProductIds(memberId: Long, productIds: List<Long>): Map<Long, Boolean>

    fun findLikedProductIdsByMemberId(memberId: Long, pageable: Pageable): Page<Long>
}
