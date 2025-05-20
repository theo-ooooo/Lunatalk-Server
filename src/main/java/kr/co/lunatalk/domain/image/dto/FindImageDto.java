package kr.co.lunatalk.domain.image.dto;


import kr.co.lunatalk.domain.image.domain.Image;

public record FindImageDto(
	String imageType,
	String imageUrl,
	String imageKey
) {
	public static FindImageDto from(Image image) {
		return new FindImageDto(image.getImageType().name(), image.getImagePath(), image.getImageKey());
	}
}
