package kr.co.lunatalk.domain.category.repository;

import kr.co.lunatalk.domain.category.domain.Category;

import java.util.Optional;

public interface CategoryRepositoryCustom {
	Optional<Category> findWithProducts(Long categoryId);
}
