package kr.co.lunatalk.domain.product.service;

import kr.co.lunatalk.domain.product.domain.Product;
import kr.co.lunatalk.domain.product.domain.ProductColor;
import kr.co.lunatalk.domain.product.dto.request.ProductCreateRequest;
import kr.co.lunatalk.domain.product.dto.request.ProductUpdateRequest;
import kr.co.lunatalk.domain.product.repository.ProductRepository;
import kr.co.lunatalk.global.exception.CustomException;
import kr.co.lunatalk.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {
	private final ProductRepository productRepository;

	public Product save(ProductCreateRequest request) {
		// 상품 저장.
		Product product = Product.createProduct(request.name(), request.price(), request.quantity(), request.status(), request.visibility());
		// 색상 저장.
		request.colors().forEach(color -> {
			ProductColor productColor = ProductColor.createProductColor(product, color);
			product.addProductColor(productColor);
		});

		productRepository.save(product);
		return product;
	}

	public void update(Long productId, ProductUpdateRequest request) {
		Product findProduct = findById(productId);
		findProduct.updateProduct(request);
	}

	public void delete(Long productId) {
		Product findProduct = findById(productId);

		findProduct.deleteProduct();
	}

	@Transactional(readOnly = true)
	public Product findById(Long id) {
		return productRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
	}
}
