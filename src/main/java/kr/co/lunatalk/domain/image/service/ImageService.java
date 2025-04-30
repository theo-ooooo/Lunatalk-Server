package kr.co.lunatalk.domain.image.service;

import kr.co.lunatalk.domain.image.domain.Image;
import kr.co.lunatalk.domain.image.domain.ImageFileExtension;
import kr.co.lunatalk.domain.image.domain.ImageType;
import kr.co.lunatalk.domain.image.dto.request.ProductImageCompletedRequest;
import kr.co.lunatalk.domain.image.dto.request.ProductImageUploadRequest;
import kr.co.lunatalk.domain.image.dto.response.PresignedUrlResponse;
import kr.co.lunatalk.domain.image.repository.ImageRepository;
import kr.co.lunatalk.domain.product.domain.Product;
import kr.co.lunatalk.domain.product.repository.ProductRepository;
import kr.co.lunatalk.global.exception.CustomException;
import kr.co.lunatalk.global.exception.ErrorCode;
import kr.co.lunatalk.global.util.SpringEnvironmentUtil;
import kr.co.lunatalk.infra.config.s3.S3Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.URL;
import java.time.Duration;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ImageService {
	private final ImageRepository imageRepository;
	private final ProductRepository productRepository;

	private final S3Presigner s3Presigner;
	private final S3Properties s3Properties;

	private final SpringEnvironmentUtil springEnvironmentUtil;


	public PresignedUrlResponse ProductImageUpload(ProductImageUploadRequest request) {

		Product product = findProductByProductId(request.productId());

		String imageKey = createImageKey();
		String imagePath = createImagePath(request.imageType(), product.getId(), imageKey, request.imageFileExtension());

		String URL = generatePresignedUrl(imagePath).toString();

		Image image = Image.createImage(request.imageType(), product.getId(), imageKey, imagePath, request.imageFileExtension());
		imageRepository.save(image);

		return PresignedUrlResponse.of(URL, image.getImageKey());
	}

	public void ProductImageCompleteUpload(ProductImageCompletedRequest request) {
		Image findImage = findImageByImageKey(request.imageKey());
		findImage.uploadedImage();
	}

	private Image findImageByImageKey(String imageKey) {
		return imageRepository.findByImageKey(imageKey).orElseThrow(() -> new CustomException(ErrorCode.IMAGE_NOT_FOUND));
	}

	private Product findProductByProductId(Long productId) {
		return productRepository.findById(productId).orElseThrow(
			() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND)
		);
	}

	private URL generatePresignedUrl(String imagePath) {
		PutObjectRequest putObjectRequest = PutObjectRequest.builder()
			.bucket(s3Properties.bucket())
			.key(imagePath)
			.build();

		PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
			.putObjectRequest(putObjectRequest)
			.signatureDuration(Duration.ofMinutes(10))
			.build();

		return s3Presigner.presignPutObject(presignRequest).url();
	}


	private String createImageKey() {
		return UUID.randomUUID().toString();
	}

	private String createImagePath(ImageType imageType, Long referenceId, String imageKey, ImageFileExtension imageFileExtension) {
		return springEnvironmentUtil.getCurrentProfile() +
			"/" +
			imageType +
			"/" +
			imageKey +
			"." +
			imageFileExtension.getUploadExtension();
	}
}
