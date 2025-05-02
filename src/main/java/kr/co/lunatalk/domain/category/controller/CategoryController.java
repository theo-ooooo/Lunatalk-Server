package kr.co.lunatalk.domain.category.controller;

import jakarta.validation.Valid;
import kr.co.lunatalk.domain.category.dto.request.CategoryAddProductRequest;
import kr.co.lunatalk.domain.category.dto.request.CategoryCreateRequest;
import kr.co.lunatalk.domain.category.dto.response.CategoryAddProductResponse;
import kr.co.lunatalk.domain.category.dto.response.CategoryCreateResponse;
import kr.co.lunatalk.domain.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {
	private final CategoryService categoryService;


	@PostMapping("/create")
	public CategoryCreateResponse create(@Valid @RequestBody CategoryCreateRequest request) {
		return categoryService.create(request);
	}

	@PostMapping("/{id}/products")
	public CategoryAddProductResponse addProduct(@PathVariable Long id, @Valid @RequestBody CategoryAddProductRequest request) {
		return categoryService.addProduct(id, request);
	}

}
