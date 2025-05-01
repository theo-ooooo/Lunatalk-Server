package kr.co.lunatalk.domain.image.service;

import kr.co.lunatalk.domain.image.domain.Image;
import kr.co.lunatalk.domain.image.domain.ImageFileExtension;
import kr.co.lunatalk.domain.image.domain.ImageStatus;
import kr.co.lunatalk.domain.image.domain.ImageType;
import kr.co.lunatalk.domain.image.dto.request.ProductImageCompletedRequest;
import kr.co.lunatalk.domain.image.dto.request.ProductImageUploadRequest;
import kr.co.lunatalk.domain.image.dto.response.PresignedUrlResponse;
import kr.co.lunatalk.domain.image.repository.ImageRepository;
import kr.co.lunatalk.domain.member.domain.MemberRole;
import kr.co.lunatalk.domain.member.repository.MemberRepository;
import kr.co.lunatalk.domain.product.domain.Product;
import kr.co.lunatalk.domain.product.domain.ProductStatus;
import kr.co.lunatalk.domain.product.domain.ProductVisibility;
import kr.co.lunatalk.domain.product.dto.request.ProductCreateRequest;
import kr.co.lunatalk.domain.product.service.ProductService;
import kr.co.lunatalk.global.exception.CustomException;
import kr.co.lunatalk.global.exception.ErrorCode;
import kr.co.lunatalk.global.security.PrincipalDetails;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ImageServiceTest {
	@Autowired private ImageService imageService;
	@Autowired private ImageRepository imageRepository;
	@Autowired private ProductService productService;


	@Test
	void 상품_이미지_PresignedUrl_생성() {
		//given
		ProductCreateRequest productCreateRequest = new ProductCreateRequest("test", 10000L, 1000, ProductStatus.ACTIVE, ProductVisibility.VISIBLE, List.of("A", "B"));
		Product saveProduct = productService.save(productCreateRequest);

		//when
		ProductImageUploadRequest request = new ProductImageUploadRequest(saveProduct.getId(), ImageType.PRODUCT_THUMBNAIL, ImageFileExtension.PNG);
		PresignedUrlResponse presignedUrlResponse = imageService.productImageUpload(request);

		//then
		assertNotNull(saveProduct);
		assertNotNull(presignedUrlResponse.presignedUrl());
	}

	@Test
	void 없는_상품으로_PresignedUrl_생성() {
		//given
		ProductImageUploadRequest request = new ProductImageUploadRequest(1L, ImageType.PRODUCT_THUMBNAIL, ImageFileExtension.PNG);
		//when, then
		assertThatThrownBy(() -> imageService.productImageUpload(request))
			.isInstanceOf(CustomException.class)
			.hasMessage(ErrorCode.PRODUCT_NOT_FOUND.getMessage());
	}

	@Test
	void 상품_이미지_등록_완료후_완료_처리() {
		//given
		ProductCreateRequest productCreateRequest = new ProductCreateRequest("test", 10000L, 1000, ProductStatus.ACTIVE, ProductVisibility.VISIBLE, List.of("A", "B"));
		Product saveProduct = productService.save(productCreateRequest);

		ProductImageUploadRequest request = new ProductImageUploadRequest(saveProduct.getId(), ImageType.PRODUCT_THUMBNAIL, ImageFileExtension.PNG);
		PresignedUrlResponse presignedUrlResponse = imageService.productImageUpload(request);
		//when
		ProductImageCompletedRequest requestComplete = new ProductImageCompletedRequest(presignedUrlResponse.imageKey());
		imageService.productImageCompleteUpload(requestComplete);
		Optional<Image> image = imageRepository.findByImageKey(presignedUrlResponse.imageKey());

		//then
		assertThat(image).isNotNull();
		assertThat(image.get().getImageStatus()).isEqualTo(ImageStatus.COMPLETED);
	}

	@Test
	void 없는_상품_이미지_키로_완료_처리() {
		//given
		ProductImageCompletedRequest requestComplete = new ProductImageCompletedRequest("TEST");
		//when, then
		assertThatThrownBy(() -> imageService.productImageCompleteUpload(requestComplete))
			.isInstanceOf(CustomException.class)
			.hasMessage(ErrorCode.IMAGE_NOT_FOUND.getMessage());
	}

	@Test
	void 이미지를_삭제_한다_성공() {
		//given
		// 상품 저장
		ProductCreateRequest productCreateRequest = new ProductCreateRequest("test", 10000L, 1000, ProductStatus.ACTIVE, ProductVisibility.VISIBLE, List.of("A", "B"));
		Product saveProduct = productService.save(productCreateRequest);

		// 이미지 업로드 url
		ProductImageUploadRequest request = new ProductImageUploadRequest(saveProduct.getId(), ImageType.PRODUCT_THUMBNAIL, ImageFileExtension.PNG);
		PresignedUrlResponse presignedUrlResponse = imageService.productImageUpload(request);
		//when
		//아마자 완료처리
		ProductImageCompletedRequest requestComplete = new ProductImageCompletedRequest(presignedUrlResponse.imageKey());
		imageService.productImageCompleteUpload(requestComplete);

		//이미지 삭제
		imageService.deleteByImageKey(presignedUrlResponse.imageKey());
		Optional<Image> image = imageRepository.findByImageKey(presignedUrlResponse.imageKey());
		//then
		assertThat(image).isNotNull();
		assertThat(image.get().getImageStatus()).isEqualTo(ImageStatus.DELETED);
	}

	@Test
	void 이미_삭제한_이미지를_삭제_요청_한다() {
		//given
		// 상품 저장
		ProductCreateRequest productCreateRequest = new ProductCreateRequest("test", 10000L, 1000, ProductStatus.ACTIVE, ProductVisibility.VISIBLE, List.of("A", "B"));
		Product saveProduct = productService.save(productCreateRequest);

		// 이미지 업로드 url
		ProductImageUploadRequest request = new ProductImageUploadRequest(saveProduct.getId(), ImageType.PRODUCT_THUMBNAIL, ImageFileExtension.PNG);
		PresignedUrlResponse presignedUrlResponse = imageService.productImageUpload(request);
		//when
		//아마자 완료처리
		ProductImageCompletedRequest requestComplete = new ProductImageCompletedRequest(presignedUrlResponse.imageKey());
		imageService.productImageCompleteUpload(requestComplete);

		//이미지 삭제
		imageService.deleteByImageKey(presignedUrlResponse.imageKey());
		// then

		assertThatThrownBy(() -> imageService.deleteByImageKey(presignedUrlResponse.imageKey()))
			.isInstanceOf(CustomException.class)
			.hasMessage(ErrorCode.IMAGE_EXISTS_DELETED.getMessage());
	}
}
