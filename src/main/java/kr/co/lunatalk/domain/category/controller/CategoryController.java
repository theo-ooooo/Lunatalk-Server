package kr.co.lunatalk.domain.category.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.co.lunatalk.domain.category.dto.request.CategoryAddProductRequest;
import kr.co.lunatalk.domain.category.dto.request.CategoryCreateRequest;
import kr.co.lunatalk.domain.category.dto.request.CategoryUpdateRequest;
import kr.co.lunatalk.domain.category.dto.response.CategoryAddProductResponse;
import kr.co.lunatalk.domain.category.dto.response.CategoryCreateResponse;
import kr.co.lunatalk.domain.category.dto.response.CategoryProductResponse;
import kr.co.lunatalk.domain.category.dto.response.CategoryResponse;
import kr.co.lunatalk.domain.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Tag(name = "카테고리 정보", description = "카테고리 관련 API")
public class CategoryController {
	private final CategoryService categoryService;

	@GetMapping()
	@Operation(summary = "카테고리 리스트", description = "카테고리 리스트를 전달합니다.")
	public List<CategoryResponse> getCategories() {
		return categoryService.getCategoryList();
	}

	@GetMapping("/{id}/products")
	@Operation(summary = "카테고리 별 상품 리스트", description = "카테고리 별 상품 리스트를 전달합니다.")
	public CategoryProductResponse getCategory(@PathVariable Long id) {
		return categoryService.getCategory(id);
	}


	@PostMapping("/create")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "카테고리 생성", description = "카테고리를 생성합니다.")
	public CategoryCreateResponse create(@Valid @RequestBody CategoryCreateRequest request) {
		return categoryService.create(request);
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "키텍리 개별 조회", description = "카테고리를 조회합니다.")
	public CategoryResponse getOneCategory(@PathVariable Long id) {
		return categoryService.getOneCategory(id);
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "카테고리 수정", description = "카테고리를 수정합니다.")
	public ResponseEntity<Void> update(@PathVariable Long id, @Valid @RequestBody CategoryUpdateRequest request) {
		categoryService.update(id, request);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "카테고리 삭제", description = "카테고리를 삭제합니다.")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		categoryService.delete(id);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/{id}/products")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "카테고리 상품 추가", description = "카테고리에 상품을 추가 합니다.")
	public CategoryAddProductResponse addProduct(@PathVariable Long id, @Valid @RequestBody CategoryAddProductRequest request) {
		return categoryService.addProduct(id, request);
	}

}
