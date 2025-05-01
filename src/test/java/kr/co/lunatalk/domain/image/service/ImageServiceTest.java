package kr.co.lunatalk.domain.image.service;

import kr.co.lunatalk.domain.image.domain.Image;
import kr.co.lunatalk.domain.image.domain.ImageFileExtension;
import kr.co.lunatalk.domain.image.domain.ImageStatus;
import kr.co.lunatalk.domain.image.domain.ImageType;
import kr.co.lunatalk.domain.image.dto.request.ProductImageCompletedRequest;
import kr.co.lunatalk.domain.image.dto.request.ProductImageUploadRequest;
import kr.co.lunatalk.domain.image.dto.response.PresignedUrlResponse;
import kr.co.lunatalk.domain.image.repository.ImageRepository;
import kr.co.lunatalk.domain.product.domain.Product;
import kr.co.lunatalk.domain.product.domain.ProductStatus;
import kr.co.lunatalk.domain.product.domain.ProductVisibility;
import kr.co.lunatalk.domain.product.repository.ProductRepository;
import kr.co.lunatalk.global.exception.CustomException;
import kr.co.lunatalk.global.exception.ErrorCode;
import kr.co.lunatalk.global.util.SpringEnvironmentUtil;
import kr.co.lunatalk.infra.config.s3.S3Properties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.URL;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

	@InjectMocks
	private ImageService imageService;

	@Mock
	private ImageRepository imageRepository;

	@Mock
	private ProductRepository productRepository;

	@Mock
	private S3Client s3Client;

	@Mock
	private S3Presigner s3Presigner;

	@Mock
	private S3Properties s3Properties;

	@Mock
	private SpringEnvironmentUtil springEnvironmentUtil;

	private Product testProduct;

	@BeforeEach
	void setUp() {
		testProduct = Product.createProduct("테스트", 10000L, 100, ProductStatus.ACTIVE, ProductVisibility.VISIBLE);
	}

	@Test
	void 상품_이미지_PresignedUrl_생성() {
		ProductImageUploadRequest request = new ProductImageUploadRequest(1L, ImageType.PRODUCT_THUMBNAIL, ImageFileExtension.PNG);
		given(productRepository.findById(anyLong())).willReturn(Optional.of(testProduct));

		URL fakeUrl = mock(URL.class);
		given(fakeUrl.toString()).willReturn("http://fake-presigned-url");
		PresignedPutObjectRequest presignedRequest = mock(PresignedPutObjectRequest.class);
		given(presignedRequest.url()).willReturn(fakeUrl);
		given(s3Presigner.presignPutObject((PutObjectPresignRequest) any())).willReturn(presignedRequest);

		PresignedUrlResponse result = imageService.productImageUpload(request);

		assertThat(result.presignedUrl()).isEqualTo("http://fake-presigned-url");
		verify(productRepository).findById(1L);
		verify(imageRepository).save(any(Image.class));
	}

	@Test
	void 없는_상품으로_PresignedUrl_생성_예외() {
		ProductImageUploadRequest request = new ProductImageUploadRequest(999L, ImageType.PRODUCT_THUMBNAIL, ImageFileExtension.PNG);
		given(productRepository.findById(anyLong())).willReturn(Optional.empty());

		assertThatThrownBy(() -> imageService.productImageUpload(request))
			.isInstanceOf(CustomException.class)
			.hasMessage(ErrorCode.PRODUCT_NOT_FOUND.getMessage());
		verify(productRepository).findById(999L);
	}

	@Test
	void 상품_이미지_업로드_완료처리() {
		Image image = Image.createImage(ImageType.PRODUCT_THUMBNAIL, 1L, "image-key", "path", ImageFileExtension.PNG);
		given(imageRepository.findByImageKey("image-key")).willReturn(Optional.of(image));

		imageService.productImageCompleteUpload(new ProductImageCompletedRequest("image-key"));

		assertThat(image.getImageStatus()).isEqualTo(ImageStatus.COMPLETED);
		verify(imageRepository).findByImageKey("image-key");
	}

	@Test
	void 없는_이미지_업로드_완료처리_예외() {
		given(imageRepository.findByImageKey("invalid-key")).willReturn(Optional.empty());

		assertThatThrownBy(() -> imageService.productImageCompleteUpload(new ProductImageCompletedRequest("invalid-key")))
			.isInstanceOf(CustomException.class)
			.hasMessage(ErrorCode.IMAGE_NOT_FOUND.getMessage());
		verify(imageRepository).findByImageKey("invalid-key");
	}

	@Test
	void 이미지_삭제_성공() {
		Image image = Image.createImage(ImageType.PRODUCT_THUMBNAIL, 1L, "image-key", "local/product/1/image-key.png", ImageFileExtension.PNG);
		given(imageRepository.findByImageKey("image-key")).willReturn(Optional.of(image));

		imageService.deleteByImageKey("image-key");

		assertThat(image.getImageStatus()).isEqualTo(ImageStatus.DELETED);
		verify(imageRepository).findByImageKey("image-key");
		verify(s3Client).deleteObject((DeleteObjectRequest) any());
	}

	@Test
	void 삭제된_이미지_다시_삭제시_예외() {
		Image image = Image.createImage(ImageType.PRODUCT_THUMBNAIL, 1L, "image-key", "local/product/1/image-key.png", ImageFileExtension.PNG);
		image.deletedImage();
		given(imageRepository.findByImageKey("deleted-key")).willReturn(Optional.of(image));

		assertThatThrownBy(() -> imageService.deleteByImageKey("deleted-key"))
			.isInstanceOf(CustomException.class)
			.hasMessage(ErrorCode.IMAGE_EXISTS_DELETED.getMessage());
		verify(imageRepository).findByImageKey("deleted-key");
	}
}
