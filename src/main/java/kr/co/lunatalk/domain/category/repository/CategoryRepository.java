package kr.co.lunatalk.domain.category.repository;

import kr.co.lunatalk.domain.category.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long>,CategoryRepositoryCustom {
}
