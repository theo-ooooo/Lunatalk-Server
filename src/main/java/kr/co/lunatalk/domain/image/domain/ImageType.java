package kr.co.lunatalk.domain.image.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ImageType {
	PRODUCT_THUMBNAIL("product_thumbnail"),
	PRODUCT_CONTENT("product_content"),
	;

	private String value;
}
