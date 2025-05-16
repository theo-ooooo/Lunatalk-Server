package kr.co.lunatalk.domain.exhibition.service;

import kr.co.lunatalk.domain.exhibition.domain.Exhibition;
import kr.co.lunatalk.domain.exhibition.domain.ExhibitionProduct;
import kr.co.lunatalk.domain.exhibition.domain.ExhibitionVisibility;
import kr.co.lunatalk.domain.exhibition.dto.request.ExhibitionCreateRequest;
import kr.co.lunatalk.domain.exhibition.dto.request.ExhibitionUpdateRequest;
import kr.co.lunatalk.domain.exhibition.dto.response.ExhibitionCreateResponse;
import kr.co.lunatalk.domain.exhibition.dto.response.ExhibitionFindOneResponse;
import kr.co.lunatalk.domain.exhibition.dto.response.ExhibitionListResponse;
import kr.co.lunatalk.domain.exhibition.repository.ExhibitionRepository;
import kr.co.lunatalk.domain.product.domain.Product;
import kr.co.lunatalk.domain.product.domain.ProductStatus;
import kr.co.lunatalk.domain.product.domain.ProductVisibility;
import kr.co.lunatalk.domain.product.dto.ProductWithImagesResult;
import kr.co.lunatalk.global.util.ProductUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExhibitionServiceTest {

	@InjectMocks
	private ExhibitionService exhibitionService;

	@Mock
	private ExhibitionRepository exhibitionRepository;

	@Mock
	private ProductUtil productUtil;

	@Test
	void createExhibition_정상생성() {
		// given
		List<Long> productIds = List.of(1L, 2L);
		ExhibitionCreateRequest request = new ExhibitionCreateRequest(
			"기획전 제목", "기획전 설명", ExhibitionVisibility.VISIBLE,
			productIds,
			LocalDateTime.now(), LocalDateTime.now().plusDays(7)
		);

		List<Product> mockProducts = productIds.stream()
			.map(id -> Product.createProduct("상품" + id, 10000L, 10, ProductStatus.ACTIVE, ProductVisibility.VISIBLE))
			.toList();

		when(productUtil.findAllProductByProductIdIn(productIds)).thenReturn(mockProducts);

		// when
		ExhibitionCreateResponse response = exhibitionService.createExhibition(request);

		// then
		assertNotNull(response);
		verify(exhibitionRepository).save(any(Exhibition.class));
	}

	@Test
	void getAllExhibitions_정상조회() {
		// given
		Exhibition exhibition = Exhibition.createExhibition(
			"기획전", "설명", ExhibitionVisibility.VISIBLE,
			LocalDateTime.now(), LocalDateTime.now().plusDays(3)
		);

		Product product = Product.createProduct("상품1", 10000L, 10, ProductStatus.ACTIVE, ProductVisibility.VISIBLE);
		ReflectionTestUtils.setField(product, "id", 1L); // ID 세팅

		ExhibitionProduct ep = ExhibitionProduct.createExhibitionProduct(exhibition, product, 1);
		exhibition.addProducts(List.of(ep));

		when(exhibitionRepository.findAll()).thenReturn(List.of(exhibition));
		when(productUtil.findAllProducts(any())).thenReturn(new ProductWithImagesResult(
			List.of(product),
			Map.of(1L, List.of())
		));

		// when
		List<ExhibitionFindOneResponse> result = exhibitionService.getAllExhibitions();

		// then
		assertEquals(1, result.size());
		verify(productUtil).findAllProducts(List.of(1L));
	}

	@Test
	void updateExhibition_정상수정() {
		// given
		Exhibition exhibition = Exhibition.createExhibition(
			"기존 제목", "기존 설명", ExhibitionVisibility.HIDDEN,
			LocalDateTime.now(), LocalDateTime.now().plusDays(3)
		);
		ReflectionTestUtils.setField(exhibition, "id", 1L);

		ExhibitionUpdateRequest request = new ExhibitionUpdateRequest(
			"수정 제목", "수정 설명", ExhibitionVisibility.VISIBLE,
			List.of(1L, 2L),
			LocalDateTime.now(), LocalDateTime.now().plusDays(10)
		);

		when(exhibitionRepository.findById(1L)).thenReturn(Optional.of(exhibition));
		when(productUtil.findAllProductByProductIdIn(any())).thenReturn(
			List.of(Product.createProduct("상품1", 10000L, 10, ProductStatus.ACTIVE, ProductVisibility.VISIBLE), Product.createProduct("상품1", 10000L, 10, ProductStatus.ACTIVE, ProductVisibility.VISIBLE))
		);

		// when
		exhibitionService.updateExhibition(1L, request);

		// then
		assertEquals("수정 제목", exhibition.getTitle());
		verify(exhibitionRepository).deleteProductByExhibitionId(1L);
	}

	@Test
	void deleteExhibition_정상삭제() {
		// given
		Exhibition exhibition = Exhibition.createExhibition(
			"삭제 기획전", "설명", ExhibitionVisibility.HIDDEN,
			LocalDateTime.now(), LocalDateTime.now().plusDays(5)
		);
		ReflectionTestUtils.setField(exhibition, "id", 1L);

		when(exhibitionRepository.findById(1L)).thenReturn(Optional.of(exhibition));

		// when
		exhibitionService.deleteExhibition(1L);

		// then
		verify(exhibitionRepository).deleteById(1L);
	}
}

