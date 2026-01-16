package kr.co.lunatalk.domain.image.domain;

import kr.co.lunatalk.global.exception.CustomException;
import kr.co.lunatalk.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum ImageFileExtension {
	PNG("png"),
	JPG("jpg"),
	JPEG("jpeg"),
	WEBP("webp"),
	;

	private String uploadExtension;

	public static ImageFileExtension of(String extension) {
		return Arrays.stream(values()).filter(
			imageFileExtension -> {
				return imageFileExtension.uploadExtension.equals(extension);
			}
		).findFirst().orElseThrow(
			() -> new CustomException(ErrorCode.IMAGE_FILE_EXTENSION_NOT_FOUND)
		);
	}

}
