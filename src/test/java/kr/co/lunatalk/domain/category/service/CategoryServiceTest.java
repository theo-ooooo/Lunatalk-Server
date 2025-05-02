package kr.co.lunatalk.domain.category.service;

import kr.co.lunatalk.domain.category.domain.Category;
import kr.co.lunatalk.domain.category.domain.CategoryStatus;
import kr.co.lunatalk.domain.category.domain.CategoryVisibility;
import kr.co.lunatalk.domain.category.dto.request.CategoryAddProductRequest;
import kr.co.lunatalk.domain.category.dto.request.CategoryCreateRequest;
import kr.co.lunatalk.domain.category.dto.request.CategoryUpdateRequest;
import kr.co.lunatalk.domain.category.dto.response.CategoryAddProductResponse;
import kr.co.lunatalk.domain.category.dto.response.CategoryCreateResponse;
import kr.co.lunatalk.domain.category.repository.CategoryRepository;
import kr.co.lunatalk.domain.product.domain.Product;
import kr.co.lunatalk.domain.product.domain.ProductStatus;
import kr.co.lunatalk.domain.product.domain.ProductVisibility;
import kr.co.lunatalk.domain.product.repository.ProductRepository;
import kr.co.lunatalk.global.exception.CustomException;
import kr.co.lunatalk.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

	@Mock
	private CategoryRepository categoryRepository;

	@Mock
	private ProductRepository productRepository;

	@InjectMocks
	private CategoryService categoryService;

	private Category testCategory;

	@BeforeEach
	void setUp() {
		testCategory = Category.createCategory("테스트", CategoryVisibility.VISIBLE);
	}


	@Test
	void 카테고리를_생성() {

		//given
		CategoryCreateRequest request = new CategoryCreateRequest("테스트", CategoryVisibility.VISIBLE);
		given(categoryRepository.existsByName(anyString())).willReturn(Boolean.FALSE);

		//when
		CategoryCreateResponse response = categoryService.create(request);

		//then
		assertNotNull(response);
		verify(categoryRepository).save(any(Category.class));
	}

	@Test
	void 이미_존재하는_카테고리_명으로_생성() {
		//given
		CategoryCreateRequest request = new CategoryCreateRequest("테스트", CategoryVisibility.VISIBLE);
		given(categoryRepository.existsByName(anyString())).willReturn(Boolean.TRUE);
		//when, then
		assertThatThrownBy(() -> categoryService.create(request))
			.isInstanceOf(CustomException.class)
			.hasMessage(ErrorCode.CATEGORY_EXISTS.getMessage());
	}

	@Test
	void 카테고리에_상품_추가() {
		//given
		List<Long> productIds = List.of(1L, 2L, 3L);
		CategoryAddProductRequest request = new CategoryAddProductRequest(productIds);

		Product product1 = Product.createProduct("P1", 1000L, 10, ProductStatus.ACTIVE, ProductVisibility.VISIBLE);
		Product product2 = Product.createProduct("P2", 2000L, 5, ProductStatus.ACTIVE, ProductVisibility.VISIBLE);
		Product product3 = Product.createProduct("P3", 3000L, 7, ProductStatus.ACTIVE, ProductVisibility.VISIBLE);
		List<Product> mockProducts = List.of(product1, product2, product3);

		given(categoryRepository.findWithProducts(anyLong())).willReturn(Optional.of(testCategory));
		given(productRepository.findAllProductsByProductIds(productIds)).willReturn(mockProducts);

		//when
		CategoryAddProductResponse response = categoryService.addProduct(1L, request);

		//then
		assertThat(testCategory.getProducts()).containsExactly(product1, product2, product3);
		assertThat(response.productIds().size()).isEqualTo(3);
		verify(productRepository).findAllProductsByProductIds(productIds);
	}

	@Test
	void 없는_카테고리에_상품_추가() {
		List<Long> productIds = List.of(1L, 2L, 3L);
		CategoryAddProductRequest request = new CategoryAddProductRequest(productIds);

		given(categoryRepository.findWithProducts(anyLong())).willReturn(Optional.empty());

		//when then
		assertThatThrownBy(() -> categoryService.addProduct(1L, request))
			.isInstanceOf(CustomException.class)
			.hasMessage(ErrorCode.CATEGORY_NOT_FOUND.getMessage());
	}

	@Test
	void 상품_업데이트() {
		//given
		CategoryUpdateRequest request = new CategoryUpdateRequest("변경", CategoryVisibility.HIDDEN);
		given(categoryRepository.findById(anyLong())).willReturn(Optional.of(testCategory));

		//when
		categoryService.update(1L, request);

		//then
		assertThat(testCategory.getName()).isEqualTo("변경");
		assertThat(testCategory.getVisibility()).isEqualTo(CategoryVisibility.HIDDEN);
	}

	@Test
	void 상품_삭제() {
		//given
		given(categoryRepository.findById(anyLong())).willReturn(Optional.of(testCategory));
		//when
		categoryService.delete(1L);
		//then
		assertThat(testCategory.getVisibility()).isEqualTo(CategoryVisibility.HIDDEN);
		assertThat(testCategory.getStatus()).isEqualTo(CategoryStatus.DELETED);
	}

}
