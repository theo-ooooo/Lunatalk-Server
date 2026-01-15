package kr.co.lunatalk.domain.productlike.service;

import kr.co.lunatalk.domain.member.domain.Member;
import kr.co.lunatalk.domain.product.domain.Product;
import kr.co.lunatalk.domain.product.dto.FindProductDto;
import kr.co.lunatalk.domain.product.dto.response.ProductFindResponse;
import kr.co.lunatalk.domain.productlike.domain.ProductLike;
import kr.co.lunatalk.domain.productlike.repository.ProductLikeRepository;
import kr.co.lunatalk.global.util.MemberUtil;
import kr.co.lunatalk.global.util.ProductUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

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

	@Transactional(readOnly = true)
	public Page<ProductFindResponse> findMyLikedProducts(Pageable pageable) {
		Member member = memberUtil.getCurrentMember();

		Page<Long> likedProductIdPage = productLikeRepository.findLikedProductIdsByMemberId(member.getId(), pageable);
		List<Long> productIds = likedProductIdPage.getContent();

		if (productIds.isEmpty()) {
			return new PageImpl<>(List.of(), pageable, likedProductIdPage.getTotalElements());
		}

		var productsWithImages = productUtil.findAllProducts(productIds);
		Map<Long, Long> likeCountMap = getLikeCounts(productIds);

		var productMap = productsWithImages.products().stream()
			.collect(Collectors.toMap(Product::getId, Function.identity(), (a, b) -> a));

		List<ProductFindResponse> content = productIds.stream()
			.map(productId -> {
				Product product = productMap.get(productId);
				if (product == null) {
					return null; // 비노출/삭제 등으로 조회되지 않는 경우
				}
				var images = productsWithImages.imageMap().getOrDefault(productId, List.of());
				Long likeCount = likeCountMap.getOrDefault(productId, 0L);
				return ProductFindResponse.from(
					FindProductDto.from(product, images, likeCount, true)
				);
			})
			.filter(Objects::nonNull)
			.toList();

		return new PageImpl<>(content, pageable, likedProductIdPage.getTotalElements());
	}
}

