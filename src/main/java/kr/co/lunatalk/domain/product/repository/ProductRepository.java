package kr.co.lunatalk.domain.product.repository;

import io.lettuce.core.dynamic.annotation.Param;
import kr.co.lunatalk.domain.product.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {

	@Modifying(clearAutomatically = true)
	@Query("UPDATE Product p SET p.category = null WHERE p.category.id = :categoryId")
	void bulkClearCategory(@Param("categoryId") Long categoryId);
}
