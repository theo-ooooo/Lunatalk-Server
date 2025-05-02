package kr.co.lunatalk.domain.product.repository;

import kr.co.lunatalk.domain.product.domain.Product;
import kr.co.lunatalk.domain.product.dto.FindProductDto;

import java.util.List;

public interface ProductRepositoryCustom {
	FindProductDto findProductById(Long productId);
	List<Product> findAllProductsByProductIds(List<Long> productIds);
}
