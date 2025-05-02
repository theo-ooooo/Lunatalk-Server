package kr.co.lunatalk.domain.category.controller;

import jakarta.validation.Valid;
import kr.co.lunatalk.domain.category.dto.request.CategoryCreateRequest;
import kr.co.lunatalk.domain.category.dto.response.CategoryCreateResponse;
import kr.co.lunatalk.domain.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {
	private final CategoryService categoryService;


	@PostMapping("/create")
	public CategoryCreateResponse create(@Valid @RequestBody CategoryCreateRequest request) {
		return categoryService.create(request);
	}

}
