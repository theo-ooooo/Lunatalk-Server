package kr.co.lunatalk.domain.category.service;

import kr.co.lunatalk.domain.category.domain.Category;
import kr.co.lunatalk.domain.category.dto.request.CategoryCreateRequest;
import kr.co.lunatalk.domain.category.dto.response.CategoryCreateResponse;
import kr.co.lunatalk.domain.category.repository.CategoryRepository;
import kr.co.lunatalk.global.exception.CustomException;
import kr.co.lunatalk.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryService {
	private final CategoryRepository categoryRepository;


	public CategoryCreateResponse create(CategoryCreateRequest request) {
		boolean isExists = existsByName(request.name());
		if (isExists) {
			throw new CustomException(ErrorCode.CATEGORY_EXISTS);
		}

		Category category = Category.createCategory(request.name(), request.visibility());
		categoryRepository.save(category);

		return CategoryCreateResponse.of(category);
	}


	@Transactional(readOnly = true)
	public Category findByName(String name) {
		return categoryRepository.findByName(name).orElseThrow(
			() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND)
		);
	}

	@Transactional(readOnly = true)
	public boolean existsByName(String name) {
		return categoryRepository.existsByName(name);
	}
}
