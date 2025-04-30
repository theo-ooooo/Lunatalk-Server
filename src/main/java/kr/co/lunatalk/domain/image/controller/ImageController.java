package kr.co.lunatalk.domain.image.controller;

import jakarta.validation.Valid;
import kr.co.lunatalk.domain.image.dto.request.ProductImageCompletedRequest;
import kr.co.lunatalk.domain.image.dto.request.ProductImageUploadRequest;
import kr.co.lunatalk.domain.image.dto.response.PresignedUrlResponse;
import kr.co.lunatalk.domain.image.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ImageController {
	private final ImageService imageService;

	@PostMapping("/products/upload-url")
	public PresignedUrlResponse productPresignedUrlCreate(@RequestBody @Valid ProductImageUploadRequest request) {
		return imageService.ProductImageUpload(request);
	}

	@PostMapping("/products/upload-complete")
	public void productImageUploaded(@RequestBody @Valid ProductImageCompletedRequest request) {
		imageService.ProductImageCompleteUpload(request);
	}
}
