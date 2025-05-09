package kr.co.lunatalk.domain.category.service;

import kr.co.lunatalk.domain.category.domain.Category;
import kr.co.lunatalk.domain.category.domain.CategoryStatus;
import kr.co.lunatalk.domain.category.domain.CategoryVisibility;
import kr.co.lunatalk.domain.category.dto.request.CategoryAddProductRequest;
import kr.co.lunatalk.domain.category.dto.request.CategoryCreateRequest;
import kr.co.lunatalk.domain.category.dto.request.CategoryUpdateRequest;
import kr.co.lunatalk.domain.category.dto.response.CategoryAddProductResponse;
import kr.co.lunatalk.domain.category.dto.response.CategoryCreateResponse;
import kr.co.lunatalk.domain.category.dto.response.CategoryProductResponse;
import kr.co.lunatalk.domain.category.dto.response.CategoryResponse;
import kr.co.lunatalk.domain.category.repository.CategoryRepository;
import kr.co.lunatalk.domain.product.domain.Product;
import kr.co.lunatalk.domain.product.dto.response.ProductFindResponse;
import kr.co.lunatalk.domain.product.repository.ProductRepository;
import kr.co.lunatalk.domain.product.service.ProductService;
import kr.co.lunatalk.global.exception.CustomException;
import kr.co.lunatalk.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryService {
	private final CategoryRepository categoryRepository;
	private final ProductRepository productRepository;
	private final ProductService productService;


	public CategoryCreateResponse create(CategoryCreateRequest request) {
		boolean isExists = existsByName(request.name());
		if (isExists) {
			throw new CustomException(ErrorCode.CATEGORY_EXISTS);
		}

		Category category = Category.createCategory(request.name(), request.visibility());
		categoryRepository.save(category);

		return CategoryCreateResponse.of(category);
	}

	public void update(Long categoryId, CategoryUpdateRequest request) {
		Category findCategory = findById(categoryId);

		findCategory.updateName(request.name());
		findCategory.updateVisibility(request.visibility());
	}

	public void delete(Long categoryId) {
		Category findCategory = findById(categoryId);

		findCategory.deleteStatus();
		findCategory.updateVisibility(CategoryVisibility.HIDDEN);
	}

	@Transactional(readOnly = true)
	public CategoryProductResponse getCategory(Long categoryId) {
		Category withProducts = categoryRepository.findWithProducts(categoryId)
			.orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

		List<Long> productIds = withProducts.getProducts().stream().map(Product::getId).toList();

		List<ProductFindResponse> allProducts = productService.findAllProducts(productIds);

		return CategoryProductResponse.of(withProducts.getId(), withProducts.getName(), allProducts);
	}

	@Transactional(readOnly = true)
	public List<CategoryResponse> getCategoryList() {
		List<Category> activeCategories = categoryRepository.findAllByStatus(CategoryStatus.ACTIVE);

		return activeCategories.stream().map(CategoryResponse::from).toList();
	}


	@Transactional(readOnly = true)
	public CategoryResponse getOneCategory(Long categoryId) {
		Category category = categoryRepository.findByIdAndStatus(categoryId, CategoryStatus.ACTIVE)
			.orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

		return CategoryResponse.from(category);
	}


	@Transactional(readOnly = true)
	public List<CategoryProductResponse> getCategoryProducts() {
		List<Category> allWithProducts = categoryRepository.findAllWithProducts();

		return allWithProducts.stream().map(category -> {
			List<Long> productIds = category.getProducts().stream().map(Product::getId).toList();

			List<ProductFindResponse> allProducts = productService.findAllProducts(productIds);

			return CategoryProductResponse.of(category.getId(), category.getName(), allProducts);
		}).toList();
	}

	public CategoryAddProductResponse addProduct(Long categoryId, CategoryAddProductRequest request) {
		Category findCategory = categoryRepository.findWithProducts(categoryId)
			.orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

		// 기존 연관 끊기.
		productRepository.bulkClearCategory(findCategory.getId());

		// 전달 받은 상품 조회. IN
		List<Product> products = productRepository.findAllProductsByProductIds(request.productIds());

		// 연결.
		// TODO: 현재 update가 단건으로 날아가는데.. 한 쿼리로 바꿔야함.
		products.forEach(findCategory::addProduct);

		return CategoryAddProductResponse.of(findCategory, products);
	}

	@Transactional(readOnly = true)
	public boolean existsByName(String name) {
		return categoryRepository.existsByName(name);
	}

	@Transactional(readOnly = true)
	public Category findById(Long id) {
		return categoryRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));
	}
}
