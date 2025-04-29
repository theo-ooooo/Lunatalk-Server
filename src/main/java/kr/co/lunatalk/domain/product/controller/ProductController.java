package kr.co.lunatalk.domain.product.controller;

import jakarta.validation.Valid;
import kr.co.lunatalk.domain.product.domain.Product;
import kr.co.lunatalk.domain.product.dto.request.ProductCreateRequest;
import kr.co.lunatalk.domain.product.dto.request.ProductUpdateRequest;
import kr.co.lunatalk.domain.product.dto.response.ProductCreateResponse;
import kr.co.lunatalk.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

	private final ProductService productService;


	@PostMapping("/create")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ProductCreateResponse create(@Valid @RequestBody ProductCreateRequest request) {
		Product product = productService.save(request);
		return ProductCreateResponse.from(product);
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<Void> update(@PathVariable Long id, @Valid @RequestBody ProductUpdateRequest request) {
		productService.update(id, request);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		productService.delete(id);
		return ResponseEntity.ok().build();
	}
}
