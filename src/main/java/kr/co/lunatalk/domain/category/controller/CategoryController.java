package kr.co.lunatalk.domain.category.controller;

import jakarta.validation.Valid;
import kr.co.lunatalk.domain.category.dto.request.CategoryAddProductRequest;
import kr.co.lunatalk.domain.category.dto.request.CategoryCreateRequest;
import kr.co.lunatalk.domain.category.dto.request.CategoryUpdateRequest;
import kr.co.lunatalk.domain.category.dto.response.CategoryAddProductResponse;
import kr.co.lunatalk.domain.category.dto.response.CategoryCreateResponse;
import kr.co.lunatalk.domain.category.dto.response.CategoryProductResponse;
import kr.co.lunatalk.domain.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {
	private final CategoryService categoryService;

	@GetMapping("/{id}/products")
	public CategoryProductResponse getCategory(@PathVariable Long id) {
		return categoryService.getCategory(id);
	}


	@PostMapping("/create")
	@PreAuthorize("hasRole('ADMIN')")
	public CategoryCreateResponse create(@Valid @RequestBody CategoryCreateRequest request) {
		return categoryService.create(request);
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Void> update(@PathVariable Long id, CategoryUpdateRequest request) {
		categoryService.update(id, request);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		categoryService.delete(id);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/{id}/products")
	@PreAuthorize("hasRole('ADMIN')")
	public CategoryAddProductResponse addProduct(@PathVariable Long id, @Valid @RequestBody CategoryAddProductRequest request) {
		return categoryService.addProduct(id, request);
	}

}
