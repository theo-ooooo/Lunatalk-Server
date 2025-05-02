package kr.co.lunatalk.domain.category.repository;

import kr.co.lunatalk.domain.category.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long>,CategoryRepositoryCustom {

	Optional<Category> findByName(String name);
	boolean existsByName(String name);
}
