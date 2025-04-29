package kr.co.lunatalk.domain.product.service;

import kr.co.lunatalk.domain.product.domain.Product;
import kr.co.lunatalk.domain.product.domain.ProductColor;
import kr.co.lunatalk.domain.product.dto.request.ProductCreateRequest;
import kr.co.lunatalk.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {
	private final ProductRepository productRepository;

	public Product save(ProductCreateRequest request) {
		// 상품 저장.
		Product product = Product.createProduct(request.name(), request.price(), request.quantity(), request.status());
		// 색상 저장.
		request.colors().forEach(color -> {
			ProductColor productColor = ProductColor.createProductColor(product, color);
			product.addProductColor(productColor);
		});

		productRepository.save(product);
		return product;
	}
}
