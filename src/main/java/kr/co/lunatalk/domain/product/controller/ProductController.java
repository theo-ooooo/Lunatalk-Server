package kr.co.lunatalk.domain.product.controller;

import jakarta.validation.Valid;
import kr.co.lunatalk.domain.product.domain.Product;
import kr.co.lunatalk.domain.product.dto.request.ProductCreateRequest;
import kr.co.lunatalk.domain.product.dto.response.ProductCreateResponse;
import kr.co.lunatalk.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

	private final ProductService productService;


	@PostMapping("/create")
	public ProductCreateResponse create(@Valid @RequestBody ProductCreateRequest request) {
		Product product = productService.save(request);
		return ProductCreateResponse.from(product);
	}
}
