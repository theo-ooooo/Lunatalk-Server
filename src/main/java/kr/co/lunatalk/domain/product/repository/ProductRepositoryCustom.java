package kr.co.lunatalk.domain.product.repository;

import kr.co.lunatalk.domain.product.domain.Product;
import kr.co.lunatalk.domain.product.dto.FindProductDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ProductRepositoryCustom {
	Optional<Product> findProductById(Long productId);
	List<Product> findAllProductsByProductIds(List<Long> productIds);
	List<Product> findAllProductDtoByIdsWithJoin(List<Long> productIds);
	Page<Product> findAll(String productName, Pageable pageable);
}
