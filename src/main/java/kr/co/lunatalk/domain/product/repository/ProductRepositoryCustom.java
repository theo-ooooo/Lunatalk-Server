package kr.co.lunatalk.domain.product.repository;

import kr.co.lunatalk.domain.product.dto.FindProductDto;

public interface ProductRepositoryCustom {
	FindProductDto findProductById(Long productId);
}
