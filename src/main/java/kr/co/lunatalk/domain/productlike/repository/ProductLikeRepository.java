package kr.co.lunatalk.domain.productlike.repository;

import kr.co.lunatalk.domain.productlike.domain.ProductLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductLikeRepository extends JpaRepository<ProductLike, Long>, ProductLikeRepositoryCustom {

	Optional<ProductLike> findByMemberIdAndProductId(Long memberId, Long productId);

	void deleteByMemberIdAndProductId(Long memberId, Long productId);
}

