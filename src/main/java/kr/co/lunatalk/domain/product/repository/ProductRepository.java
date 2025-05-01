package kr.co.lunatalk.domain.product.repository;

import kr.co.lunatalk.domain.product.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {
}
