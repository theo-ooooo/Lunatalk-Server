package kr.co.lunatalk.domain.productlike.repository

import kr.co.lunatalk.domain.productlike.domain.ProductLike
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface ProductLikeRepository : JpaRepository<ProductLike, Long>, ProductLikeRepositoryCustom {

    fun findByMemberIdAndProductId(memberId: Long, productId: Long): Optional<ProductLike>

    fun deleteByMemberIdAndProductId(memberId: Long, productId: Long)
}
