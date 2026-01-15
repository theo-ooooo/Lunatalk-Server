package kr.co.lunatalk.domain.productlike.repository;

import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductLikeRepositoryCustom {

	Long countByProductId(Long productId);

	Map<Long, Long> countByProductIds(List<Long> productIds);

	Map<Long, Boolean> existsByMemberIdAndProductIds(Long memberId, List<Long> productIds);

	Page<Long> findLikedProductIdsByMemberId(Long memberId, Pageable pageable);
}

