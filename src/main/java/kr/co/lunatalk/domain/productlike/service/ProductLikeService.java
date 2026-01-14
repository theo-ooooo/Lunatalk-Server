package kr.co.lunatalk.domain.productlike.service;

import kr.co.lunatalk.domain.member.domain.Member;
import kr.co.lunatalk.domain.product.domain.Product;
import kr.co.lunatalk.domain.product.repository.ProductRepository;
import kr.co.lunatalk.domain.productlike.domain.ProductLike;
import kr.co.lunatalk.domain.productlike.repository.ProductLikeRepository;
import kr.co.lunatalk.global.exception.CustomException;
import kr.co.lunatalk.global.exception.ErrorCode;
import kr.co.lunatalk.global.util.MemberUtil;
import kr.co.lunatalk.global.util.ProductUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductLikeService {

	private final ProductLikeRepository productLikeRepository;
	private final MemberUtil memberUtil;
	private final ProductUtil productUtil;

	public void toggleLike(Long productId) {
		Member member = memberUtil.getCurrentMember();
		Product product = productUtil.findProductId(productId);

		Optional<ProductLike> existingLike = productLikeRepository.findByMemberIdAndProductId(
			member.getId(), productId
		);

		if (existingLike.isPresent()) {
			productLikeRepository.delete(existingLike.get());
		} else {
			ProductLike productLike = ProductLike.create(member, product);
			productLikeRepository.save(productLike);
		}
	}

	@Transactional(readOnly = true)
	public Long getLikeCount(Long productId) {
		Long count = productLikeRepository.countByProductId(productId);
		return count != null ? count : 0L;
	}

	@Transactional(readOnly = true)
	public Map<Long, Long> getLikeCounts(List<Long> productIds) {
		return productLikeRepository.countByProductIds(productIds);
	}

	@Transactional(readOnly = true)
	public boolean isLiked(Long productId, Long memberId) {
		if (memberId == null) {
			return false;
		}
		return productLikeRepository.findByMemberIdAndProductId(memberId, productId).isPresent();
	}

	@Transactional(readOnly = true)
	public Map<Long, Boolean> getLikedStatus(List<Long> productIds, Long memberId) {
		if (memberId == null) {
			return productIds.stream()
				.collect(java.util.stream.Collectors.toMap(id -> id, id -> false));
		}
		return productLikeRepository.existsByMemberIdAndProductIds(memberId, productIds);
	}
}

